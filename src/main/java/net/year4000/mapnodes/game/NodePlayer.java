package net.year4000.mapnodes.game;

import lombok.Data;
import net.year4000.mapnodes.api.events.player.GamePlayerJoinEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerJoinSpectatorEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerJoinTeamEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerStartEvent;
import net.year4000.mapnodes.api.game.GameClass;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.GameTeam;
import net.year4000.mapnodes.api.utils.Spectator;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.NMSHacks;
import net.year4000.utilities.bukkit.BadgeManager;
import net.year4000.mapnodes.utils.Common;
import net.year4000.mapnodes.utils.PacketHacks;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.utilities.bukkit.MessageUtil;
import net.year4000.utilities.bukkit.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

@Data
public final class NodePlayer implements GamePlayer, Comparable {
    // internals
    public static final BadgeManager badges = new BadgeManager();
    private static final int INV_SIZE = 45;
    private final NodeGame game;
    private final Player player;
    private NodeTeam team;
    private NodeTeam pendingTeam;
    private NodeClass classKit;
    private List<BukkitTask> playerTasks = new ArrayList<>();
    private boolean immortal;
    // scoreboard
    private Scoreboard scoreboard;
    // player flags (set by methods bellow)
    private boolean spectator;
    private boolean playing;
    private boolean entering;
    private Inventory inventory;

    /** Constructs a game player */
    public NodePlayer(NodeGame game, Player player) {
        this.game = game;
        this.player = player;
        scoreboard = game.getScoreboardFactory().createScoreboard(this);
        player.setScoreboard(scoreboard);
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

            sendMessage(Common.textLine(Msg.locale(player, "map.created") + game.getMap().author(player.getLocale()), size, ' ', "", "&7&o"));
            game.getMap().getMultiLineDescription(player.getLocale(), 6)
                .forEach(string -> sendMessage(Common.textLine(string, size, ' ', "", "&a&o")));

            if (start.getMessage().size() > 0) {
                sendMessage("");
                start.getMessage().forEach(string -> sendMessage(Common.textLine(string, size, ' ', "", "&a&o")));
            }

            sendMessage("&7&m******************************************");
            sendMessage("");
        }

        // Player Settings
        player.setCollidesWithEntities(true);
        player.getPlayer().setDisplayName(getPlayerColor() + ChatColor.WHITE.toString());
        reopenPlayerInventory();
        updateHiddenSpectator();
        updateInventory();
    }

    public void join() {
        playing = false;

        joinTeam(null);

        inventory = Bukkit.createInventory(null, INV_SIZE, getBadge() + " " + getPlayerColor());

        if (PacketHacks.isTitleAble(player.getPlayer())) {
            PacketHacks.setTitle(
                player.getPlayer(),
                "&a" + game.getMap().getName(),
                Msg.locale(player, "map.created") + game.getMap().author(player.getPlayer().getLocale()),
                0,
                55,
                0
            );

            PacketHacks.setTabListHeadFoot(player, game.getTabHeader(), game.getTabFooter());
        }
        else {
            sendMessage(Msg.NOTICE + Msg.locale(player, "game.new.recommend"));
        }

        GamePlayerJoinEvent join = new GamePlayerJoinEvent(this) {{
            this.setSpawn(game.getConfig().getSafeRandomSpawn());
            this.setMenu(!game.getStage().isEndGame());
        }};
        join.call();

        // run a tick later to allow player to login
        player.teleport(join.getSpawn());

        // Update player's info
        playerTasks.add(SchedulerUtil.runAsync(() -> {
            game.getPlayers().map(player -> (NodePlayer) player).forEach(player -> {
                game.getScoreboardFactory().setOrUpdateListName(player, this);
                game.getScoreboardFactory().setOrUpdateListName(this, player);
            });
        }, 25L));

        // start menu
        playerTasks.add(SchedulerUtil.runAsync(() -> {
            if (join.isMenu() && player.getOpenInventory().getType() == InventoryType.CRAFTING) {
                game.openTeamChooserMenu(this);
            }

            if (!game.getStage().isEndGame()) {
                game.getScoreboardFactory().setPersonalSidebar(this);
            }
        }, 55L));
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

        // Update sidebar when there is a sidebar to update
        if (scoreboard.getObjective(DisplaySlot.SIDEBAR) != null) {
            game.getScoreboardFactory().setAllPersonalSidebar();
        }

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

            // Update sidebar when there is a sidebar to update
            if (scoreboard.getObjective(DisplaySlot.SIDEBAR) != null) {
                game.getScoreboardFactory().setAllPersonalSidebar();
            }

            // Kit
            joinSpectator.getKit().giveKit(this);

            // Spectator Settings
            player.setCollidesWithEntities(false);
            updateHiddenSpectator();
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
        }

        // Update player's inventory
        // reopenPlayerInventory();
        // Update team menu
        game.updateTeamChooserMenu();
        player.getPlayer().setDisplayName(getPlayerColor() + ChatColor.WHITE.toString());
    }

    public void joinSpectatorTeam() {
        joinTeam(null);
    }

    /** Manage how the players see each other. */
    public void updateHiddenSpectator() {
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

    /** Reopen player's inventory when they switch teams */
    public void reopenPlayerInventory() {
        if (inventory != null) {
            List<HumanEntity> viewers = inventory.getViewers();
            viewers.forEach(HumanEntity::closeInventory);
            inventory = Bukkit.createInventory(null, INV_SIZE, getBadge() + " " + getPlayerColor());
            viewers.forEach(e -> e.openInventory(inventory));
        }
    }

    /** Create an inventory of the player stats. */
    public void updateInventory() {
        playerTasks.add(SchedulerUtil.runSync(() -> {
            ItemStack[] items = new ItemStack[INV_SIZE];
            Player player = getPlayer();
            PlayerInventory pinv = player.getInventory();

            // Armor
            items[0] = NMSHacks.setSkullSkin(NMSHacks.makeSkull(player, getBadge() + " " + getPlayerColor()), player);
            items[1] = pinv.getHelmet();
            items[2] = pinv.getChestplate();
            items[3] = pinv.getLeggings();
            items[4] = pinv.getBoots();

            // Health and Food
            items[8] = player.isDead() ? new ItemStack(Material.AIR) : getHunger();
            items[7] = player.isDead() ? new ItemStack(Material.AIR) : getHealth();

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

            inventory.setContents(items);
        }, 3L));
    }

    /** Get the heal for the player. */
    private ItemStack getHealth() {
        int health = (int) player.getHealth();

        ItemStack level = new ItemStack(Material.SPECKLED_MELON, health);
        ItemMeta meta = level.getItemMeta();
        meta.setDisplayName(MessageUtil.message(Msg.locale(player, "inv.health")));
        level.setItemMeta(meta);

        return level;
    }

    /** Get the hunger for the player. */
    private ItemStack getHunger() {
        int hunger = player.getFoodLevel();

        ItemStack level = new ItemStack(Material.COOKED_BEEF, hunger);
        ItemMeta meta = level.getItemMeta();
        meta.setDisplayName(MessageUtil.message(Msg.locale(player, "inv.hunger")));
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
        return badges.getBadge(player);
    }

    /** Get the badge rank of this player */
    public int getBadgeRank() {
        return badges.findBadge(player).getRank();
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
}
