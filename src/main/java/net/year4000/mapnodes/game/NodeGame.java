package net.year4000.mapnodes.game;

import com.google.common.base.Joiner;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.NodeFactory;
import net.year4000.mapnodes.api.events.game.*;
import net.year4000.mapnodes.api.exceptions.InvalidJsonException;
import net.year4000.mapnodes.api.game.*;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeConfig;
import net.year4000.mapnodes.api.game.regions.Region;
import net.year4000.mapnodes.api.game.scoreboard.GameSidebarGoal;
import net.year4000.mapnodes.api.utils.Operations;
import net.year4000.mapnodes.api.utils.Spectator;
import net.year4000.mapnodes.api.utils.Validator;
import net.year4000.mapnodes.clocks.Clocker;
import net.year4000.mapnodes.clocks.NextNode;
import net.year4000.mapnodes.clocks.RestartServer;
import net.year4000.mapnodes.clocks.StartGame;
import net.year4000.mapnodes.game.regions.RegionEvents;
import net.year4000.mapnodes.game.scoreboard.ScoreboardFactory;
import net.year4000.mapnodes.game.scoreboard.SidebarGoal;
import net.year4000.mapnodes.messages.Message;
import net.year4000.mapnodes.messages.MessageManager;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Common;
import net.year4000.mapnodes.utils.MathUtil;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.mapnodes.utils.TimeUtil;
import net.year4000.mapnodes.utils.typewrappers.GameSet;
import net.year4000.utilities.bukkit.BukkitUtil;
import net.year4000.utilities.bukkit.ItemUtil;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Getter
@Setter
@NoArgsConstructor
public final class NodeGame implements GameManager, Validator {
    /** Details about the current map. */
    @Since(1.0)
    private NodeMap map = null;
    /** General game settings. */
    @Since(1.0)
    @SerializedName("world")
    private NodeConfig config = null;
    /** The mini locale system built right into the map.json */
    @Since(1.0)
    private Map<String, Map<String, String>> locales = new ConcurrentHashMap<>();
    @Since(1.0)
    @SerializedName("games")
    private GameSet<GameMode> gameModes = new GameSet<>();
    /** Manage the items and effects that are given to the player. */
    @Since(1.0)
    private Map<String, NodeKit> kits = new ConcurrentHashMap<>();
    /** Manage the regions and zones that can apply effects. */
    @Since(1.0)
    private Map<String, NodeRegion> regions = new ConcurrentHashMap<>();
    /** Manages the teams. */
    @Since(1.0)
    private Map<String, NodeTeam> teams = new ConcurrentHashMap<>();
    /** Classes to pick from on top of your team. */
    @Since(1.0)
    private Map<String, NodeClass> classes = new ConcurrentHashMap<>();

    @Override
    public void validate() throws InvalidJsonException {
        // Required component
        checkNotNull(map != null, Msg.util("settings.map"));

        // Validate component
        map.validate();

        // Required component
        checkNotNull(config != null, Msg.util("settings.game"));

        // Validate component
        config.validate();

        // Required component
        checkNotNull(gameModes != null, Msg.util("settings.modes"));

        // Validate components
        gameModes.stream()
            .map(GameMode::getRawConfig)
            .collect(Collectors.toList())
            .forEach(GameModeConfig::validate);

        // Required component
        checkArgument(teams.size() != 0, Msg.util("settings.team"));

        // Validate components
        teams.values().forEach(GameTeam::validate);

        // Validate components
        classes.values().forEach(GameClass::validate);
    }

    /*//--------------------------------------------//
         Upper Json Settings / Bellow Instance Code
    *///--------------------------------------------//

    private transient static final Joiner pageJoiner = Joiner.on('\n');
    private transient Map<Player, GamePlayer> players = new ConcurrentHashMap<>();
    private transient NodeStage stage = NodeStage.LOADING;
    private transient BukkitTask gameClock;
    private transient StartGame startClock;
    private transient BukkitTask stopClock;
    private transient Map<Locale, Inventory> teamChooser = new HashMap<>();
    private transient Map<Locale, Inventory> classKitChooser = new HashMap<>();
    private transient ScoreboardFactory scoreboardFactory;
    private transient Map<String, GameSidebarGoal> sidebarGoals = new LinkedHashMap<>();
    private transient List<Operations> startControls = new CopyOnWriteArrayList<>();
    private transient long startTime = 0, stopTime;
    @Setter(AccessLevel.NONE)
    private transient int baseStartTime = 10;

    /** The start time for the game */
    public void addStartTime(int time) {
        baseStartTime += time;
    }

    /** Init things that happen before load is playable */
    public void load() {
        MapNodesPlugin.debug(Msg.util("node.game.load"));
        scoreboardFactory = new ScoreboardFactory(this);

        // Assign this game to child objects
        map.assignNodeGame(this);
        teams.values().forEach(team -> team.assignNodeGame(this));
        regions.values().forEach(team -> team.assignNodeGame(this));
        kits.values().forEach(team -> team.assignNodeGame(this));
        classes.values().forEach(team -> team.assignNodeGame(this));

        // Register game mode listeners
        gameModes.forEach(NodeModeFactory.get()::registerListeners);

        // Call the game load event
        new GameLoadEvent(this).call();

        // Create GUI for all locales
        if (MessageManager.get().getLocales().size() == 0) {
            Locale defaultLocale = new Locale(Message.DEFAULT_LOCALE);
            teamChooser.put(defaultLocale, createTeamChooserMenu(defaultLocale));
            classKitChooser.put(defaultLocale, createClassKitChooserMenu(defaultLocale));
        }
        else {
            for (Locale locale : MessageManager.get().getLocales().keySet()) {
                teamChooser.put(locale, createTeamChooserMenu(locale));
                classKitChooser.put(locale, createClassKitChooserMenu(locale));
            }
        }


        // Run heavy resource tasks
        regions.values().parallelStream().forEach(region -> region.getZoneSet().forEach(Region::getPoints));

        // If startControls are empty add a default one
        if (startControls.size() == 0) {
            startControls.add(() -> {
                int size = (int) teams.values().stream().filter(team -> !(team instanceof Spectator)).filter(team -> team.getPlayers().size() > 0).count();
                return size >= (int) teams.values().stream().filter(team -> !(team instanceof Spectator)).count();
            });
        }

        stage = NodeStage.WAITING;
    }

    /** Load a team in to the system */
    public void loadTeam(String id, @Nonnull GameTeam team) {
        checkArgument(stage == NodeStage.LOADING, Msg.util("load.only.loading"));

        teams.putIfAbsent(id, (NodeTeam) team);
    }

    /** Load a kit in to the system */
    public void loadKit(String id, @Nonnull GameKit kit) {
        checkArgument(stage == NodeStage.LOADING, Msg.util("load.only.loading"));

        kits.putIfAbsent(id, (NodeKit) kit);
    }

    /** Load a class in to the system */
    public void loadClass(String id, @Nonnull GameClass clazz) {
        checkArgument(stage == NodeStage.LOADING, Msg.util("load.only.loading"));

        classes.putIfAbsent(id, (NodeClass) clazz);
    }

    /** Load a class in to the system */
    public void loadRegion(String id, @Nonnull GameRegion region) {
        checkArgument(stage == NodeStage.LOADING, Msg.util("load.only.loading"));

        regions.putIfAbsent(id, (NodeRegion) region);
    }

    /** Add start control operation */
    public void addStartControl(Operations operation) {
        startControls.add(operation);
    }

    // START Sidebar Things //

    /** Add a dynamic goal to the scoreboard */
    public GameSidebarGoal addDynamicGoal(String id, String display, int score) {
        GameSidebarGoal goal = sidebarGoals.put(id, new SidebarGoal(this, GameSidebarGoal.GoalType.DYNAMIC, display, score, ""));
        scoreboardFactory.setAllGameSidebar();
        return goal;
    }

    /** Add a dynamic goal to the scoreboard */
    public GameSidebarGoal addDynamicGoal(String id, String owner, String display, int score) {
        GameSidebarGoal goal = sidebarGoals.put(id, new SidebarGoal(this, SidebarGoal.GoalType.DYNAMIC, display, score, owner));
        scoreboardFactory.setAllGameSidebar();
        return goal;
    }

    /** Add a static foal to the scoreboard */
    public GameSidebarGoal addStaticGoal(String id, String display) {
        GameSidebarGoal goal = sidebarGoals.put(id, new SidebarGoal(this, SidebarGoal.GoalType.STATIC, display, null, ""));
        scoreboardFactory.setAllGameSidebar();
        return goal;
    }

    /** Add a static foal to the scoreboard */
    public GameSidebarGoal addStaticGoal(String id, String owner, String display) {
        GameSidebarGoal goal = sidebarGoals.put(id, new SidebarGoal(this, SidebarGoal.GoalType.STATIC, display, null, owner));
        scoreboardFactory.setAllGameSidebar();
        return goal;
    }

    // END Sidebar Things //

    // START Class GUI //

    /** Create the menu in locale */
    private Inventory createClassKitChooserMenu(Locale locale) {
        int base = BukkitUtil.invBase(classes.size());
        Inventory inv = Bukkit.createInventory(null, base, Common.truncate(Msg.locale(locale.toString(), "class.menu.title"), 32));

        ItemStack[] items = classes.values().stream()
            .map(clazz -> clazz)
            .map(clazz -> clazz.createClassIcon(locale))
            .collect(Collectors.toList())
            .toArray(new ItemStack[base]);

        SchedulerUtil.runSync(() -> inv.setContents(items));

        return inv;
    }

    /** Open the menu if they can */
    public void openClassKitChooserMenu(GamePlayer player) {
        Locale locale = new Locale(player.getPlayer().getLocale());
        Inventory menu = MessageManager.get().isLocale(player.getPlayer().getLocale()) ? classKitChooser.get(locale) : classKitChooser.get(new Locale(Message.DEFAULT_LOCALE));

        if (!stage.isEndGame()) {
            player.getPlayer().openInventory(menu);
        }
        else {
            player.sendMessage(Msg.NOTICE + Msg.locale(player, "class.menu.not_now"));
        }
    }

    // END Class GUI //

    // START Team GUI //

    /** Create the menu in locale */
    private Inventory createTeamChooserMenu(Locale locale) {
        Inventory inv = Bukkit.createInventory(null, BukkitUtil.invBase(teams.size()), Common.truncate(Msg.locale(locale.toString(), "team.menu.title"), 32));
        updateTeamChooserMenu(locale, inv);
        return inv;
    }

    /** Update the team chooser menu for all locales */
    void updateTeamChooserMenu() {
        teamChooser.forEach(this::updateTeamChooserMenu);
    }

    /** Update the team chooser when a player join a new team */
    private void updateTeamChooserMenu(Locale locale, Inventory inv) {
        int base = BukkitUtil.invBase(this.teams.size() + 1);
        ItemStack[] items = new ItemStack[base];
        ItemStack rand = new ItemStack(Material.NETHER_STAR);
        int teams = 1;

        rand.setItemMeta(ItemUtil.addMeta(rand, String.format(
            "{display:{name:\"%s\",lore:[\"%s&7/&6%s\",\"%s\"]}}",
            Msg.locale(locale.toString(), "team.menu.join.random"),
            Common.colorCapacity((int) getPlaying().count() + (int) getEntering().count(), getMaxPlayers()),
            getMaxPlayers(),
            Msg.locale(locale.toString(), "team.menu.join")
        )));
        items[0] = rand;

        Collection<GameTeam> onlySpectator = new ArrayList<>(getTeams().values());
        onlySpectator.removeAll(getPlayingTeams().collect(Collectors.toList()));

        for (GameTeam team : this.teams.values().size() == 2 ? onlySpectator : this.teams.values()) {
            int position = team instanceof Spectator ? base - 1 : teams;
            items[position] = ((NodeTeam) team).getTeamIcon(locale);
            teams++;
        }

        SchedulerUtil.runSync(() -> inv.setContents(items));
    }

    /** Open the menu if they can */
    public void openTeamChooserMenu(GamePlayer player) {
        Locale locale = new Locale(player.getPlayer().getLocale());
        Inventory menu = MessageManager.get().isLocale(player.getPlayer().getLocale()) ? teamChooser.get(locale) : teamChooser.get(new Locale(Message.DEFAULT_LOCALE));

        if (!stage.isEndGame()) {
            player.getPlayer().openInventory(menu);
        }
        else {
            player.sendMessage(Msg.NOTICE + Msg.locale(player, "team.menu.not_now"));
        }
    }

    // END Team GUI //

    // START Mini Locale Manager //

    /** Get the locale wanted or try to default to en_US or use any locale */
    public String defaultLocale(String key) {
        return locale(Message.DEFAULT_LOCALE, key);
    }

    /** Get the locale wanted or try to default to en_US or use any locale */
    public String locale(Locale locale, String key) {
        return locale(locale.toString(), key);
    }

    /** Get the locale wanted or try to default to en_US or use any locale */
    public String locale(String code, String key) {
        if (locales.size() > 0) {
            if (locales.containsKey(code)) {
                return locales.get(code).getOrDefault(key, Msg.locale(code, key));
            }
            else if (locales.containsKey(Message.DEFAULT_LOCALE)) {
                return locales.get(Message.DEFAULT_LOCALE).getOrDefault(key, Msg.locale(code, key));
            }
            else {
                return locales.values().stream().collect(Collectors.toList()).get(0).getOrDefault(key, Msg.locale(code, key));
            }
        }
        else {
            return Msg.locale(code, key);
        }
    }

    // END Mini Locale Manager //

    // START Game Components Handlers //

    public Stream<GamePlayer> getPlayers() {
        return players.values().stream();
    }

    public Stream<GamePlayer> getPlaying() {
        return getPlayers().filter(GamePlayer::isPlaying);
    }

    public Stream<GamePlayer> getSpectating() {
        return getPlayers().filter(GamePlayer::isSpectator);
    }

    public Stream<GamePlayer> getEntering() {
        return getPlayers().filter(GamePlayer::isEntering);
    }

    public GamePlayer getPlayer(Player player) {
        return players.get(player);
    }

    public Stream<GameTeam> getPlayingTeams() {
        return teams.values().stream()
            .filter(team -> !(team instanceof Spectator))
            .map(team -> (GameTeam) team);
    }

    /** Checks if the player can join the specific team */
    public GameTeam checkAndGetTeam(GamePlayer player, String name) throws IllegalArgumentException {
        Locale locale = new Locale(player.getPlayer().getLocale());
        if (Msg.matches(locale.toString(), name, "team.menu.join.random") || MessageUtil.stripColors(name).equalsIgnoreCase("Spectator")) {
            return getTeam(locale, name);
        }
        else {
            checkArgument(Common.isVIP(player.getPlayer()), Msg.locale(player, "team.select.non_vip"));
            return getTeam(locale, name);
        }
    }

    public GameTeam getTeam(Locale locale, String name) {
        // Random get lowest team
        if (Msg.matches(locale.toString(), name, "team.menu.join.random")) {
            return teams.values().stream()
                .filter(team -> !(team instanceof Spectator))
                .sorted((left, right) -> left.getPlayers().size() < right.getPlayers().size() ? -1 : 1)
                .collect(Collectors.toList())
                .get(0);
        }
        // Get by name
        else {
            List<GameTeam> stream = teams.values().stream()
                .filter(team -> team.getDisplayName().equalsIgnoreCase(name))
                .collect(Collectors.toList());

            if (stream.size() == 0) {
                return getTeam(locale, Msg.locale(locale.toString(), "team.menu.join.random"));
            }
            else {
                return stream.get(0);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, GameClass> getClasses() {
        return (Map) classes;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, GameTeam> getTeams() {
        return (Map) teams;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, GameRegion> getRegions() {
        return (Map) regions;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, GameKit> getKits() {
        return (Map) kits;
    }

    public GameClass getClassKit(String name) {
        List<GameClass> stream = classes.values().stream()
            .filter(clazz -> clazz.getName().equalsIgnoreCase(MessageUtil.stripColors(name)))
            .collect(Collectors.toList());

        return stream.get(0);
    }

    public int getMaxPlayers() {
        int count = 0;

        for (GameTeam team : teams.values().stream().filter(t -> !(t instanceof Spectator)).collect(Collectors.toList())) {
            count += team.getSize();
        }

        return count;
    }

    public int getRealMaxCount() {
        return getMaxPlayers() + teams.size();
    }

    public String getTabHeader(GamePlayer player) {
        String header = getMap().title();

        if (stage.isPlaying() && player != null) {
            long currentTime = Common.cleanTimeMillis() - getStartTime();
            String time = "&a" + (new TimeUtil(currentTime, TimeUnit.MILLISECONDS)).prettyOutput("&7:&a");

            header = getMap().title() + " &7- " + Msg.locale(player, "game.time", time);
        }
        else if (stage.isEndGame() && player != null) {
            String time = "&a" + (new TimeUtil(getStopTime() - getStartTime(), TimeUnit.MILLISECONDS)).prettyOutput("&7:&a");

            header = getMap().title() + " &7- " + Msg.locale(player, "game.time", time);
        }

        return stage.getStageColor() + MessageUtil.replaceColors(header).substring(2);
    }

    public String getTabHeader() {
        return getTabHeader(null);
    }

    public String getTabFooter(String logo) {
        logo = logo == null ? "&3[&bYear4000&3]" : logo;
        return MessageUtil.replaceColors("&b" + MapNodesPlugin.getInst().getNetwork().getName() + " &7- " + logo + " &7- &bmc&7.&byear4000&7.&bnet");
    }

    public String getTabFooter() {
        return getTabFooter(null);
    }

    // END Game Components Handlers //

    // START Game Controls //

    public void join(Player player) {
        players.put(player, new NodePlayer(this, player));
        ((NodePlayer) getPlayer(player)).join();
    }

    public void quit(Player player) {
        ((NodePlayer) getPlayer(player)).leave();
        players.remove(player);
    }

    /** Should the game start depending all start operations are true */
    public boolean shouldStart() {
        return startControls.parallelStream().filter(Operations::handle).count() > 0L;
    }

    /** Start the game */
    public void start() {
        if (stage == NodeStage.PLAYING) {
            return;
        }

        (new PreGameStartEvent(this)).call();

        stage = NodeStage.PLAYING;

        GameStartEvent start = new GameStartEvent(this);
        start.call();

        // Register region events
        regions.values().stream()
            .map(NodeRegion::getEvents)
            .filter(e -> e != null)
            .forEach(RegionEvents::registerEvents);

        // Assign the events reference the the region they belong to
        regions.values().stream()
            .filter(region -> region.getEvents() != null)
            .forEach(region -> region.getEvents().assignRegion((NodeRegion) region));

        // Close spectator inventories
        Stream.concat(start.getGame().getSpectating(), start.getGame().getEntering()).forEach(player -> player.getPlayer().closeInventory());
        start.getGame().getEntering()
            .filter(player -> !(((NodePlayer) player).getPendingTeam()).getQueue().contains(player))
            .forEach(player -> ((NodePlayer) player).start());

        gameClock = SchedulerUtil.repeatAsync(() -> new GameClockEvent(this).call(), 20L);
        startTime = Common.cleanTimeMillis();
    }

    /** Stop the game and cycle to the next with default time */
    public void stop() {
        stop(null);
    }

    /** Stop the game anc cycle to the next with a time */
    public void stop(@Nullable Integer time) {
        if (stage == NodeStage.ENDING) {
            return;
        }

        stage = NodeStage.ENDING;
        stopTime = Common.cleanTimeMillis();

        if (startTime == 0) {
            startTime = stopTime - 1;
        }

        if (gameClock != null) {
            gameClock.cancel();
        }

        GameStopEvent stop = new GameStopEvent(this);
        stop.call();

        stop.getGame().getPlayers().forEach(player -> {
            player.getPlayer().closeInventory();
            player.joinSpectatorTeam();
            ((NodeGame) stop.getGame()).getScoreboardFactory().setGameSidebar((NodePlayer) player);
        });

        // Unregister region events
        regions.values().stream()
            .map(region -> region.getEvents())
            .filter(e -> e != null)
            .forEach(RegionEvents::unregisterEvents);

        // Unregister game mode listeners
        gameModes.forEach(NodeModeFactory.get()::unregisterListeners);

        // Cycle game or restart server
        new Clocker(165) {
            String shortMapName = Common.shortMessage(22, getMap().getName());
            String title = shortMapName;

            @Override
            public void runTock(int position) {
                getPlayers()
                    .map(GamePlayer::getPlayer)
                    .map(Player::getScoreboard)
                    .map(obj -> obj.getObjective(DisplaySlot.SIDEBAR))
                    .filter(obj -> obj != null)
                    .forEach(obj -> {
                        int pos = (int) (((MathUtil.percent(getTime(), position) * 10) / 10) * (title.length() * .01));
                        String parts = title.substring(0, pos) + "&3" + (pos == title.length() ? title.substring(pos) : title.charAt(pos) + "&f" + title.substring(pos + 1));
                        obj.setDisplayName(Common.truncate(MessageUtil.replaceColors("    &b" + parts + "    "), 32));
                    });
            }

            @Override
            public void runLast(int position) {
                getPlayers()
                    .map(GamePlayer::getPlayer)
                    .map(Player::getScoreboard)
                    .map(obj -> obj.getObjective(DisplaySlot.SIDEBAR))
                    .filter(obj -> obj != null)
                    .forEach(obj -> obj.setDisplayName(Common.truncate(MessageUtil.replaceColors("    &f" + title + "    "), 32)));

                SchedulerUtil.runAsync(() -> {
                    getPlayers()
                        .map(GamePlayer::getPlayer)
                        .map(Player::getScoreboard)
                        .map(obj -> obj.getObjective(DisplaySlot.SIDEBAR))
                        .filter(obj -> obj != null)
                        .forEach(Objective::unregister);

                    if (NodeFactory.get().isQueuedGames()) {
                        if (time != null) {
                            stopClock = new NextNode(time).run();
                        }
                        else {
                            stopClock = new NextNode().run();
                        }
                    }
                    else {
                        if (time != null) {
                            stopClock = new RestartServer(time).run();
                        }
                        else {
                            stopClock = new RestartServer().run();
                        }
                    }
                }, 20L);
            }
        }.run();
    }

    // STOP Game Controls //

    /** Get the pages for a book with the map's info */
    public List<String> getBookPages(Player player) {
        List<String> pages = new ArrayList<>();

        pages.add(pageJoiner.join(map.getBookPage(player)));
        pages.add(pageJoiner.join(NodeTeam.getBookPage(player)));

        return pages;
    }
}
