/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.game;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.year4000.mapnodes.api.events.player.GamePlayerJoinEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerJoinSpectatorEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerJoinTeamEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerStartEvent;
import net.year4000.mapnodes.api.game.GameClass;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.GameTeam;
import net.year4000.mapnodes.api.utils.Spectator;
import net.year4000.mapnodes.backend.AccountCache;
import net.year4000.mapnodes.backend.MapNodesBadgeManager;
import net.year4000.mapnodes.messages.MessageManager;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Common;
import net.year4000.mapnodes.utils.NMSHacks;
import net.year4000.mapnodes.utils.PacketHacks;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.utilities.bukkit.MessageUtil;
import net.year4000.utilities.bukkit.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.year4000.utilities.locale.AbstractLocaleManager.toLanguage;

public final class NodePlayer implements GamePlayer, Comparable {
    // internals
    public static final MapNodesBadgeManager badges = new MapNodesBadgeManager();
    private static final int INV_SIZE = 45;
    private final NodeGame game;
    private final Player player;
    private NodeTeam team;
    private NodeTeam pendingTeam;
    private NodeClass classKit;
    private List<BukkitTask> playerTasks = Lists.newCopyOnWriteArrayList();
    private boolean immortal;
    private Map<Class, Object> playerData = Maps.newConcurrentMap();
    // scoreboard
    private Scoreboard scoreboard;
    // player flags (set by methods bellow)
    private boolean spectator;
    private boolean playing;
    private boolean entering;
    private Map<Locale, Inventory> inventory = Maps.newConcurrentMap();
    // backend database
    private AccountCache cache;
    private AtomicInteger creditsMultiplier = new AtomicInteger(1);

    /** Constructs a game player */
    public NodePlayer(NodeGame game, Player player) {
        this.game = game;
        this.player = player;
        this.cache = AccountCache.getAccount(this);
        scoreboard = game.getScoreboardFactory().createScoreboard(this);
        player.setScoreboard(scoreboard);
    }

    /** Add an object to player data */
    @Override
    public void addPlayerData(Class clazz, Object object) {
        checkNotNull(clazz);
        checkNotNull(object);

        playerData.put(clazz, object);
    }

    /** Add an object to player data */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getPlayerData(Class clazz) {
        checkNotNull(clazz);

        return (T) playerData.get(clazz);
    }

    /** Add an object to player data */
    @Override
    public void removePlayerData(Class clazz) {
        checkNotNull(clazz);

        playerData.remove(clazz);
    }

    /** Add one to token and experience multiplier */
    @Override
    public int addMultiplierModifier() {
        return getCreditsMultiplier().incrementAndGet();
    }

    /** Get the locale of the player */
    public Locale getLocale() {
        return toLanguage(new Locale(player.spigot().getLocale()));
    }

    /** Get the locale of the player */
    public String getRawLocale() {
        return getLocale().toString();
    }

    /** Does this player have a class kit */
    public boolean hasClassKit() {
        return classKit != null;
    }

    /** Set the class kit for this player */
    public void setClassKit(GameClass classKit) {
        sendMessage(Msg.locale(player, "class.join", "&a" + classKit.getName()));
        this.classKit = (NodeClass) classKit;
        game.getScoreboardFactory().setPersonalSidebar(this);
    }

    public void start() {
        if (pendingTeam != null) {
            // If you are in the team's queue don't start
            if (!pendingTeam.getQueue().contains(this)) {
                team = pendingTeam;
            }

            pendingTeam = null;
        }

        playing = true;
        entering = false;

        GamePlayerStartEvent start = new GamePlayerStartEvent(this) {{
            this.setImmortal(true);
            this.setKit(hasClassKit() ? classKit.getKit() : team.getKit());
            this.setTeam(team);
            this.setSpawn(team.getSpawns().getSafeRandomSpawn());
        }};
        start.call();

        // Event Method Results
        game.getScoreboardFactory().setTeam(this, (NodeTeam) start.getTeam());
        game.getScoreboardFactory().setGameSidebar(this);
        start.getKit().giveKit(this);
        player.teleport(start.getSpawn());

        // God buffer mode
        if (immortal = start.isImmortal()) {
            playerTasks.add(NodeKit.immortal(player));
        }

        // Game start message
        if (start.getMessage() != null) {
            final int size = 45;
            sendMessage("");
            sendMessage(Common.textLine(game.getMap().title(), 40, '*'));

            sendMessage(Common.textLine(Msg.locale(player, "map.created") + game.getMap().author(getRawLocale()), size, ' ', "", "&7&o"));
            game.getMap().getMultiLineDescription(getRawLocale(), 6)
                .forEach(string -> sendMessage(Common.textLine(string, size, ' ', "", "&a&o")));

            if (start.getMessage().size() > 0) {
                sendMessage("");
                start.getMessage().forEach(string -> sendMessage(Common.textLine(string, size, ' ', "", "&a&o")));
            }

            sendMessage("&7&m******************************************");
            sendMessage("");
        }

        // Player Settings
        player.spigot().setCollidesWithEntities(true);
        player.getPlayer().setDisplayName(getPlayerColor() + ChatColor.WHITE.toString());
        updateHiddenSpectator();
        updateInventories();

        start.runPostEvents();
    }

    public void join() {
        playing = false;

        joinTeam(null);

        GamePlayerJoinEvent join = new GamePlayerJoinEvent(this) {{
            this.setSpawn(game.getConfig().getSafeRandomSpawn());
            this.setMenu(!game.getStage().isEndGame());
        }};
        join.call();

        // run a tick later to allow player to login
        if (!join.getSpawn().equals(player.getPlayer().getLocation())) {
            player.teleport(join.getSpawn());
        }

        // Run player events later
        join.addPostEvent(() -> {
            updateInventories();
            game.getScoreboardFactory().setTeam(this, team);

            PacketHacks.setTitle(
                player.getPlayer(),
                "&a" + game.getMap().getName(),
                Msg.locale(player, "map.created") + game.getMap().author(getRawLocale()),
                20,
                60,
                20
            );

            PacketHacks.setTabListHeadFoot(player, game.getTabHeader(), game.getTabFooter());
        });

        // start menu
        join.addPostEvent(() -> {
            synchronized (game) {
                if (join.isMenu() && player.getOpenInventory().getType() == InventoryType.CRAFTING) {
                    if (game.getStage().isPreGame()) {
                        try {
                            String random = Msg.locale(this, "team.menu.join.random");
                            GameTeam team = game.checkAndGetTeam(this, random);
                            joinTeam(team);
                        }
                        catch (IllegalArgumentException e) {
                            // Can not auto join team give menu
                            game.openTeamChooserMenu(this);
                        }
                    }
                    else {
                        game.openTeamChooserMenu(this);
                    }
                }

                game.getScoreboardFactory().setPersonalSidebar(this);
            }
        });

        playerTasks.add(SchedulerUtil.runAsync(join::runPostEvents, 60L));
    }

    public void leave() {
        playing = false;

        if (team != null) {
            team.leave(this);
        }

        if (pendingTeam != null) {
            pendingTeam.leave(this);
            pendingTeam = null;
        }

        game.getScoreboardFactory().setAllPersonalSidebar();

        // Cancel tasks
        playerTasks.stream().forEach(BukkitTask::cancel);
        BossBar.removeBar(player);
        // Update team menu
        game.updateTeamChooserMenu();
    }

    public void joinTeam(GameTeam gameTeam) {
        // Init team join spectator
        if (team == null || gameTeam == null) {
            if (team != null) {
                leave();
            }

            spectator = true;
            entering = false;

            // Join
            GameTeam spectatorTeam = game.getTeams().get(NodeTeam.SPECTATOR);
            GamePlayerJoinSpectatorEvent joinSpectator = new GamePlayerJoinSpectatorEvent(this, spectatorTeam) {{
                this.setDisplay(false);
                this.setKit(spectatorTeam.getKit());
            }};
            joinSpectator.call();
            team = (NodeTeam) joinSpectator.getSpectator();
            team.join(this, joinSpectator.isDisplay());

            // Auto join spectator team
            game.getScoreboardFactory().setTeam(this, team);
            game.getScoreboardFactory().setAllPersonalSidebar();

            // Kit
            joinSpectator.getKit().giveKit(this);

            // Spectator Settings
            player.spigot().setCollidesWithEntities(false);
            updateHiddenSpectator();
            joinSpectator.runPostEvents();
        }
        // join new team
        else {
            if (gameTeam instanceof Spectator) {
                joinTeam(null);
                return;
            }

            GamePlayerJoinTeamEvent joinTeam = new GamePlayerJoinTeamEvent(this, team) {{
                this.setTo(gameTeam);
                this.setJoining(game.getStage().isPlaying());
                this.setDisplay(true);
            }};
            joinTeam.call();

            // if event is canceled set player back to spectator
            if (joinTeam.isCancelled()) {
                joinTeam(null);
                return;
            }

            leave();

            spectator = false;
            entering = true;
            pendingTeam = (NodeTeam) joinTeam.getTo();
            pendingTeam.join(this, joinTeam.isDisplay());
            game.getScoreboardFactory().setAllPersonalSidebar();

            if (joinTeam.isJoining()) {
                pendingTeam.start(this);
            }

            joinTeam.runPostEvents();
        }

        // Update player's inventory
        // reopenPlayerInventory();

        // Update network level and experience
        player.setLevel(cache.getLevel());
        player.setTotalExperience(cache.getExperience());
        player.setExp(cache.getNextExperienceLevel());

        // Update team menu
        game.updateTeamChooserMenu();
        player.getPlayer().setDisplayName(getPlayerColor() + ChatColor.WHITE.toString());
    }

    public void joinSpectatorTeam() {
        joinTeam(null);
    }

    /** Manage how the players see each other. */
    public void updateHiddenSpectator() {
        synchronized (game.getPlayers()) {
            game.getPlayers().forEach(gPlayer -> {
                game.getPlayers().forEach(player -> {
                    if ((player.isSpectator() || player.isEntering()) && gPlayer.isPlaying()) {
                        gPlayer.getPlayer().hidePlayer(player.getPlayer());
                    }
                    else {
                        gPlayer.getPlayer().showPlayer(player.getPlayer());
                    }
                });
            });
        }
    }

    /** Get the inventory for the player by locale */
    public Inventory getInventory(Locale locale) {
        return Preconditions.checkNotNull(inventory.get(locale));
    }

    /** Update all know inventories for that locale */
    public void updateInventories() {
        MessageManager.get().getLocales().keySet().forEach(locale -> {
            inventory.putIfAbsent(locale, Bukkit.createInventory(null, INV_SIZE, getBadge() + " " + getPlayerColor()));

            if (!inventory.get(locale).getTitle().equals(getBadge() + " " + getPlayerColor())) {
                inventory.put(locale, Bukkit.createInventory(null, INV_SIZE, getBadge() + " " + getPlayerColor()));
            }

            updateInventory(locale);
        });
    }

    /** Create an inventory of the player stats. */
    public void updateInventory(Locale locale) {
        playerTasks.add(SchedulerUtil.runAsync(() -> {
            ItemStack[] items = new ItemStack[INV_SIZE];
            Player player = getPlayer();
            PlayerInventory pinv = player.getInventory();

            // Head and Stats
            ItemStack head = NMSHacks.setSkullSkin(NMSHacks.makeSkull(player, getBadge() + " " + getPlayerColor()), player);
            ItemMeta meta = head.getItemMeta();
            List<String> lines = new ArrayList<>();
            lines.add(Msg.locale(locale.toString(), "team.name") + " " + team.getDisplayName());

            if (hasClassKit()) {
                lines.add(Msg.locale(locale.toString(), "class.name") + " " + classKit.getName());
            }

            lines.add("");
            lines.add(MessageUtil.replaceColors("&6&o" + player.getName() + ".y4k.me"));
            meta.setLore(lines);
            head.setItemMeta(meta);
            items[0] = head;

            // Armor
            items[1] = pinv.getHelmet();
            items[2] = pinv.getChestplate();
            items[3] = pinv.getLeggings();
            items[4] = pinv.getBoots();

            // Health and Food
            items[8] = player.isDead() ? new ItemStack(Material.AIR) : getHunger(locale);
            items[7] = player.isDead() ? new ItemStack(Material.AIR) : getHealth(locale);

            // Items
            for (int i = 0; i < 36; i++) {
                // Hot Bar
                if (i < 9) {
                    boolean empty = pinv.getItem(i) == null;
                    items[(45 - 9) + i] = empty ? new ItemStack(Material.AIR) : pinv.getItem(i);
                }
                // Backpack
                else {
                    boolean empty = pinv.getItem(i) == null;
                    items[i] = empty ? new ItemStack(Material.AIR) : pinv.getItem(i);
                }
            }

            inventory.get(locale).setContents(items);
        }, 3L));
    }

    /** Get the heal for the player. */
    private ItemStack getHealth(Locale locale) {
        int health = (int) player.getHealth();

        ItemStack level = new ItemStack(Material.SPECKLED_MELON, health);
        ItemMeta meta = level.getItemMeta();
        meta.setDisplayName(MessageUtil.message(Msg.locale(locale.toString(), "inv.health")));
        level.setItemMeta(meta);

        return level;
    }

    /** Get the hunger for the player. */
    private ItemStack getHunger(Locale locale) {
        int hunger = player.getFoodLevel();

        ItemStack level = new ItemStack(Material.COOKED_BEEF, hunger);
        ItemMeta meta = level.getItemMeta();
        meta.setDisplayName(MessageUtil.message(Msg.locale(locale.toString(), "inv.hunger")));
        level.setItemMeta(meta);

        return level;
    }

    /** Send a message with out grabing the player's instance first */
    public void sendMessage(String message, Object... args) {
        player.sendMessage(MessageUtil.message(message, args));
    }

    /** Send a message with out grabing the player's instance first */
    public void sendMessage(List<String> messages) {
        messages.forEach(this::sendMessage);
    }

    /** Send a message to the Action Bar */
    public void sendActionbarMessage(String message, Object... args) {
        PacketHacks.sendActionBarMessage(player, MessageUtil.message(message, args));
    }

    /** Get the player's color according to the team */
    public String getPlayerColor() {
        return MessageUtil.replaceColors(team.getColor() + player.getName());
    }

    /** Get the badge of this player */
    public String getBadge() {
        return badges.getBadge(this);
    }

    /** Get the badge rank of this player */
    public int getBadgeRank() {
        return badges.findBadge(this).getRank();
    }

    /** Get the split name used to tab list name */
    public String[] getSplitName() {
        String name = MessageUtil.stripColors(player.getDisplayName());
        return new String[]{name.substring(0, name.length() - 2), name.substring(name.length() - 2)};
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof NodePlayer)) {
            return -1;
        }

        return Common.isVIP(player) ? 1 : -1;
    }

    public NodeGame getGame() {
        return this.game;
    }

    public Player getPlayer() {
        return this.player;
    }

    public NodeTeam getTeam() {
        return this.team;
    }

    public NodeTeam getPendingTeam() {
        return this.pendingTeam;
    }

    public NodeClass getClassKit() {
        return this.classKit;
    }

    public List<BukkitTask> getPlayerTasks() {
        return this.playerTasks;
    }

    public boolean isImmortal() {
        return this.immortal;
    }

    public Map<Class, Object> getPlayerData() {
        return this.playerData;
    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    public boolean isSpectator() {
        return this.spectator;
    }

    public boolean isPlaying() {
        return this.playing;
    }

    public boolean isEntering() {
        return this.entering;
    }

    public Map<Locale, Inventory> getInventory() {
        return this.inventory;
    }

    public AccountCache getCache() {
        return this.cache;
    }

    public AtomicInteger getCreditsMultiplier() {
        return this.creditsMultiplier;
    }

    public void setTeam(NodeTeam team) {
        this.team = team;
    }

    public void setPendingTeam(NodeTeam pendingTeam) {
        this.pendingTeam = pendingTeam;
    }

    public void setPlayerTasks(List<BukkitTask> playerTasks) {
        this.playerTasks = playerTasks;
    }

    public void setImmortal(boolean immortal) {
        this.immortal = immortal;
    }

    public void setPlayerData(Map<Class, Object> playerData) {
        this.playerData = playerData;
    }

    public void setScoreboard(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public void setSpectator(boolean spectator) {
        this.spectator = spectator;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public void setEntering(boolean entering) {
        this.entering = entering;
    }

    public void setInventory(Map<Locale, Inventory> inventory) {
        this.inventory = inventory;
    }

    public void setCache(AccountCache cache) {
        this.cache = cache;
    }

    public void setCreditsMultiplier(AtomicInteger creditsMultiplier) {
        this.creditsMultiplier = creditsMultiplier;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof NodePlayer)) return false;
        final NodePlayer other = (NodePlayer) o;
        final Object this$game = this.getGame();
        final Object other$game = other.getGame();
        if (this$game == null ? other$game != null : !this$game.equals(other$game)) return false;
        final Object this$player = this.getPlayer();
        final Object other$player = other.getPlayer();
        if (this$player == null ? other$player != null : !this$player.equals(other$player)) return false;
        final Object this$team = this.getTeam();
        final Object other$team = other.getTeam();
        if (this$team == null ? other$team != null : !this$team.equals(other$team)) return false;
        final Object this$pendingTeam = this.getPendingTeam();
        final Object other$pendingTeam = other.getPendingTeam();
        if (this$pendingTeam == null ? other$pendingTeam != null : !this$pendingTeam.equals(other$pendingTeam))
            return false;
        final Object this$classKit = this.getClassKit();
        final Object other$classKit = other.getClassKit();
        if (this$classKit == null ? other$classKit != null : !this$classKit.equals(other$classKit)) return false;
        final Object this$playerTasks = this.getPlayerTasks();
        final Object other$playerTasks = other.getPlayerTasks();
        if (this$playerTasks == null ? other$playerTasks != null : !this$playerTasks.equals(other$playerTasks))
            return false;
        if (this.isImmortal() != other.isImmortal()) return false;
        final Object this$playerData = this.getPlayerData();
        final Object other$playerData = other.getPlayerData();
        if (this$playerData == null ? other$playerData != null : !this$playerData.equals(other$playerData))
            return false;
        final Object this$scoreboard = this.getScoreboard();
        final Object other$scoreboard = other.getScoreboard();
        if (this$scoreboard == null ? other$scoreboard != null : !this$scoreboard.equals(other$scoreboard))
            return false;
        if (this.isSpectator() != other.isSpectator()) return false;
        if (this.isPlaying() != other.isPlaying()) return false;
        if (this.isEntering() != other.isEntering()) return false;
        final Object this$inventory = this.getInventory();
        final Object other$inventory = other.getInventory();
        if (this$inventory == null ? other$inventory != null : !this$inventory.equals(other$inventory)) return false;
        final Object this$cache = this.getCache();
        final Object other$cache = other.getCache();
        if (this$cache == null ? other$cache != null : !this$cache.equals(other$cache)) return false;
        final Object this$creditsMultiplier = this.getCreditsMultiplier();
        final Object other$creditsMultiplier = other.getCreditsMultiplier();
        if (this$creditsMultiplier == null ? other$creditsMultiplier != null : !this$creditsMultiplier.equals(other$creditsMultiplier))
            return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $game = this.getGame();
        result = result * PRIME + ($game == null ? 43 : $game.hashCode());
        final Object $player = this.getPlayer();
        result = result * PRIME + ($player == null ? 43 : $player.hashCode());
        final Object $team = this.getTeam();
        result = result * PRIME + ($team == null ? 43 : $team.hashCode());
        final Object $pendingTeam = this.getPendingTeam();
        result = result * PRIME + ($pendingTeam == null ? 43 : $pendingTeam.hashCode());
        final Object $classKit = this.getClassKit();
        result = result * PRIME + ($classKit == null ? 43 : $classKit.hashCode());
        final Object $playerTasks = this.getPlayerTasks();
        result = result * PRIME + ($playerTasks == null ? 43 : $playerTasks.hashCode());
        result = result * PRIME + (this.isImmortal() ? 79 : 97);
        final Object $playerData = this.getPlayerData();
        result = result * PRIME + ($playerData == null ? 43 : $playerData.hashCode());
        final Object $scoreboard = this.getScoreboard();
        result = result * PRIME + ($scoreboard == null ? 43 : $scoreboard.hashCode());
        result = result * PRIME + (this.isSpectator() ? 79 : 97);
        result = result * PRIME + (this.isPlaying() ? 79 : 97);
        result = result * PRIME + (this.isEntering() ? 79 : 97);
        final Object $inventory = this.getInventory();
        result = result * PRIME + ($inventory == null ? 43 : $inventory.hashCode());
        final Object $cache = this.getCache();
        result = result * PRIME + ($cache == null ? 43 : $cache.hashCode());
        final Object $creditsMultiplier = this.getCreditsMultiplier();
        result = result * PRIME + ($creditsMultiplier == null ? 43 : $creditsMultiplier.hashCode());
        return result;
    }

    public String toString() {
        return "net.year4000.mapnodes.game.NodePlayer(game=" + this.getGame() + ", player=" + this.getPlayer() + ", team=" + this.getTeam() + ", pendingTeam=" + this.getPendingTeam() + ", classKit=" + this.getClassKit() + ", playerTasks=" + this.getPlayerTasks() + ", immortal=" + this.isImmortal() + ", playerData=" + this.getPlayerData() + ", scoreboard=" + this.getScoreboard() + ", spectator=" + this.isSpectator() + ", playing=" + this.isPlaying() + ", entering=" + this.isEntering() + ", inventory=" + this.getInventory() + ", cache=" + this.getCache() + ", creditsMultiplier=" + this.getCreditsMultiplier() + ")";
    }
}
