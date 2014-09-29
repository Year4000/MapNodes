package net.year4000.mapnodes.game;

import com.google.common.base.Joiner;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;
import lombok.*;
import net.year4000.mapnodes.NodeFactory;
import net.year4000.mapnodes.api.events.game.GameClockEvent;
import net.year4000.mapnodes.api.events.game.GameLoadEvent;
import net.year4000.mapnodes.api.events.game.GameStartEvent;
import net.year4000.mapnodes.api.events.game.GameStopEvent;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.GameTeam;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeConfig;
import net.year4000.mapnodes.api.utils.Operations;
import net.year4000.mapnodes.clocks.NextNode;
import net.year4000.mapnodes.clocks.RestartServer;
import net.year4000.mapnodes.clocks.StartGame;
import net.year4000.mapnodes.exceptions.InvalidJsonException;
import net.year4000.mapnodes.game.regions.Region;
import net.year4000.mapnodes.game.regions.RegionEvents;
import net.year4000.mapnodes.game.scoreboard.ScoreboardFactory;
import net.year4000.mapnodes.game.scoreboard.SidebarGoal;
import net.year4000.mapnodes.game.system.Spectator;
import net.year4000.mapnodes.messages.Message;
import net.year4000.mapnodes.messages.MessageManager;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Common;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.mapnodes.utils.Validator;
import net.year4000.mapnodes.utils.typewrappers.GameSet;
import net.year4000.utilities.bukkit.BukkitUtil;
import net.year4000.utilities.bukkit.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
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
    @SerializedName("game")
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
        for (GameModeConfig mode : gameModes.stream().map(GameMode::getConfig).collect(Collectors.toList())) {
            mode.validate();
        }

        // Required component
        checkArgument(teams.size() != 0, Msg.util("settings.team"));

        // Validate components
        for (NodeTeam team : teams.values()) {
            team.validate();
        }

        // Validate components
        for (NodeClass clazz : classes.values()) {
            clazz.validate();
        }
    }

    /*//--------------------------------------------//
         Upper Json Settings / Bellow Instance Code
    *///--------------------------------------------//

    private transient static final Joiner pageJoiner = Joiner.on('\n');
    private transient Map<UUID, GamePlayer> players = new ConcurrentHashMap<>();
    private transient NodeStage stage = NodeStage.WAITING;
    private transient BukkitTask gameClock;
    private transient StartGame startClock;
    private transient BukkitTask stopClock;
    private transient Map<Locale, Inventory> teamChooser = new HashMap<>();
    private transient ScoreboardFactory scoreboardFactory;
    private transient Map<String, SidebarGoal> sidebarGoals = new HashMap<>();
    private transient List<Operations> startControls = new CopyOnWriteArrayList<>();
    private transient long startTime;

    /** Init things that happen before load is playable */
    public void load() {
        scoreboardFactory = new ScoreboardFactory(this);

        // Assign this game to child objects
        map.assignNodeGame(this);
        teams.values().forEach(team -> team.assignNodeGame(this));
        regions.values().forEach(team -> team.assignNodeGame(this));
        kits.values().forEach(team -> team.assignNodeGame(this));
        classes.values().forEach(team -> team.assignNodeGame(this));

        // Register game mode listeners
        gameModes.forEach(m -> NodeModeFactory.get().registerListeners(m));

        // Call the game load event
        new GameLoadEvent(this).call();

        // Create GUI for all locales
        for (Locale locale : MessageManager.get().getLocales().keySet()) {
            teamChooser.put(locale, createTeamChooserMenu(locale));
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
    }

    /** Add start control operation */
    public void addStartControl(Operations operation) {
        startControls.add(operation);
    }

    // START Sidebar Things //

    /** Add a dynamic goal to the scoreboard */
    public SidebarGoal addDynamicGoal(String id, String display, int score) {
        return sidebarGoals.put(id, new SidebarGoal(SidebarGoal.GoalType.DYNAMIC, display, score));
    }

    /** Add a static foal to the scoreboard */
    public SidebarGoal addStaticGoal(String id, String display) {
        return sidebarGoals.put(id, new SidebarGoal(SidebarGoal.GoalType.STATIC, display, null));
    }

    // END Sidebar Things //

    // START Class GUI //

    // todo Class GUIs

    // END Class GUI //

    // START Team GUI //

    /** Create the menu in locale */
    private Inventory createTeamChooserMenu(Locale locale) {
        Inventory inv = Bukkit.createInventory(null, BukkitUtil.invBase(teams.size()), Msg.locale(locale.toString(), "team.menu.title"));
        updateTeamChooserMenu(locale, inv);
        return inv;
    }

    /** Update the team chooser menu for all locales */
    public void updateTeamChooserMenu() {
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

        for (GameTeam team : this.teams.values()) {
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
                return locales.get(code).getOrDefault(key, key);
            }
            else if (locales.containsKey(Message.DEFAULT_LOCALE)) {
                return locales.get(Message.DEFAULT_LOCALE).getOrDefault(key, key);
            }
            else {
                return locales.values().stream().collect(Collectors.toList()).get(0).getOrDefault(key, key);
            }
        }
        else {
            return key;
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
        return players.get(player.getUniqueId());
    }

    public Stream<NodeTeam> getPlayingTeams() {
        return teams.values().stream().filter(team -> !(team instanceof Spectator));
    }

    public GameTeam getTeam(Locale locale, String name) {
        // Random get lowest team
        if (name.equalsIgnoreCase(Msg.locale(locale.toString(), "team.menu.join.random"))) {
            return teams.values().stream()
                .filter(team -> !(team instanceof Spectator))
                .sorted((left, right) -> left.getPlayers().size() < right.getPlayers().size() ? -1 : 1)
                .collect(Collectors.toList())
                .get(0);
        }
        // Get by name
        else {
            List<NodeTeam> stream = teams.values().stream()
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

    public int getMaxPlayers() {
        int count = 0;

        for (GameTeam team : teams.values().stream().filter(t -> !(t instanceof Spectator)).collect(Collectors.toList())) {
            count += team.getSize();
        }

        return count;
    }

    // END Game Components Handlers //

    // START Game Controls //

    public void join(Player player) {
        players.put(player.getUniqueId(), new NodePlayer(this, player));
        ((NodePlayer) getPlayer(player)).join();
    }

    public void quit(Player player) {
        ((NodePlayer) getPlayer(player)).leave();
        players.remove(player.getUniqueId());
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

        stage = NodeStage.PLAYING;

        GameStartEvent start = new GameStartEvent(this);
        start.call();

        // Register region events
        regions.values().stream()
            .map(NodeRegion::getEvents)
            .filter(e -> e != null)
            .forEach(RegionEvents::registerEvents);

        // Assign the events reference the the region they belong to
        regions.values().stream().filter(region -> region.getEvents() != null).forEach(region -> region.getEvents().assignRegion(region));

        // Close spectator inventories
        start.getGame().getSpectating().forEach(player -> player.getPlayer().closeInventory());
        start.getGame().getEntering().forEach(player -> player.getPlayer().closeInventory());
        start.getGame().getEntering().forEach(player -> ((NodePlayer) player).start());

        gameClock = SchedulerUtil.repeatAsync(() -> new GameClockEvent(this).call(), 20L);
        startTime = System.currentTimeMillis();
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

        if (gameClock != null) {
            gameClock.cancel();
        }

        GameStopEvent stop = new GameStopEvent(this);
        stop.call();

        Stream.concat(stop.getGame().getPlaying(), stop.getGame().getEntering()).forEach(player -> ((NodePlayer) player).joinTeam(null));

        // Unregister region events
        regions.values().stream()
            .map(NodeRegion::getEvents)
            .filter(e -> e != null)
            .forEach(RegionEvents::unregisterEvents);

        // Unregister game mode listeners
        gameModes.forEach(m -> NodeModeFactory.get().unregisterListeners(m));

        // Cycle game or restart server
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