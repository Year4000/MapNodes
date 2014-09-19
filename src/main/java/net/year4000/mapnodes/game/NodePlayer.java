package net.year4000.mapnodes.game;

import lombok.Data;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.events.player.GamePlayerJoinEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerJoinSpectatorEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerJoinTeamEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerStartEvent;
import net.year4000.mapnodes.api.game.GameMap;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.GameTeam;
import net.year4000.mapnodes.clocks.Clocker;
import net.year4000.mapnodes.game.system.Spectator;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Common;
import net.year4000.mapnodes.utils.MathUtil;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.utilities.bukkit.FunEffectsUtil;
import net.year4000.utilities.bukkit.MessageUtil;
import net.year4000.utilities.bukkit.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.year4000.mapnodes.utils.MathUtil.percent;
import static net.year4000.mapnodes.utils.MathUtil.ticks;

@Data
public final class NodePlayer implements GamePlayer {
    // internals
    private Player player;
    private NodeTeam team;
    private List<BukkitTask> playerTasks = new ArrayList<>();

    // player flags (set by methods bellow)
    private boolean spectator;
    private boolean playing;
    private boolean entering;

    private static final int INV_SIZE = 45;
    private Inventory inventory;

    /** Constructs a game player */
    public NodePlayer(Player player) {
        this.player = player;
    }

    public void start() {
        playing = true;
        entering = false;

        GamePlayerStartEvent start = new GamePlayerStartEvent(this) {{
            this.setImmortal(true);
            this.setKit(team.getKit());
            this.setTeam(team);
            this.setSpawn(team.getSafeRandomSpawn());
            this.setMessage(new ArrayList<String>() {{
                add("&7&m****&a&l Game Started &7&m****"); // TODO proper start message
            }});
        }};
        start.call();

        // team start
        ((NodeTeam) start.getTeam()).start(this);

        // team kit
        ((NodeKit) start.getTeam().getKit()).giveKit(this);

        // spawn tp
        player.teleport(start.getSpawn());

        // God buffer mode
        if (start.isImmortal()) {
            playerTasks.add(NodeKit.immortal(player));
        }

        // game start message
        if (start.getMessage() != null) {
            start.getMessage().forEach(this::sendMessage);
        }
    }

    public void join() {
        playing = false;

        joinTeam(null);

        inventory = Bukkit.createInventory(null, INV_SIZE, getPlayerColor());

        GamePlayerJoinEvent join = new GamePlayerJoinEvent(this) {{
            this.setSpawn(((NodeGame) MapNodes.getCurrentGame()).getConfig().getSafeRandomSpawn());
            this.setMenu(!MapNodes.getCurrentGame().getStage().isEndGame());
        }};
        join.call();

        // run a tick later to allow player to login
        player.teleport(join.getSpawn());
        player.setBedSpawnLocation(join.getSpawn(), true);

        // start menu
        playerTasks.add(SchedulerUtil.runAsync(() -> {
            if (join.isMenu()) {
                ((NodeGame) MapNodes.getCurrentGame()).openTeamChooserMenu(this);
            }
        }, 10L));
    }

    public void leave() {
        playing = false;

        if (team != null) {
            team.leave(this);
        }

        // Cancel tasks
        playerTasks.stream().forEach(BukkitTask::cancel);
        BossBar.removeBar(player);
        // Update team menu
        ((NodeGame) MapNodes.getCurrentGame()).updateTeamChooserMenu();
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
            GameTeam spectatorTeam = MapNodes.getCurrentGame().getTeams().get("spectator");
            GamePlayerJoinSpectatorEvent joinSpectator = new GamePlayerJoinSpectatorEvent(this, spectatorTeam) {{
                this.setDisplay(false);
                this.setKit(spectatorTeam.getKit());
            }};
            joinSpectator.call();
            team = (NodeTeam)joinSpectator.getSpectator();
            team.join(this, joinSpectator.isDisplay());
            team.start(this);

            // Kit
            ((NodeKit) joinSpectator.getKit()).giveKit(this);
        }
        // join new team
        else {
            if (gameTeam instanceof Spectator) {
                joinTeam(null);
                return;
            }

            GamePlayerJoinTeamEvent joinTeam = new GamePlayerJoinTeamEvent(this, team) {{
                this.setTo(gameTeam);
                this.setJoining(MapNodes.getCurrentGame().getStage().isPlaying());
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
            team = (NodeTeam) joinTeam.getTo();
            team.join(this, joinTeam.isDisplay());

            if (joinTeam.isJoining()) {
                GamePlayer gamePlayer = this;

                Clocker join = new Clocker(MathUtil.ticks(10)) {
                    private Integer[] ticks = {
                        ticks(5),
                        ticks(4),
                        ticks(3),
                        ticks(2),
                        ticks(1)
                    };

                    public void runFirst(int position) {
                        FunEffectsUtil.playSound(player, Sound.ORB_PICKUP);
                    }

                    public void runTock(int position) {
                        GameMap map = MapNodes.getCurrentGame().getMap();

                        if (Arrays.asList(ticks).contains(position)) {
                            FunEffectsUtil.playSound(player, Sound.NOTE_PLING);
                        }

                        BossBar.setMessage(
                            player,
                            Msg.locale(player, "clocks.join.tock", map.getName(), Common.colorNumber(sec(position), sec(getTime()))),
                            percent(getTime(), position)
                        );
                    }

                    public void runLast(int position) {
                        FunEffectsUtil.playSound(player, Sound.NOTE_BASS);
                        BossBar.setMessage(player, Msg.locale(player, "clocks.join.last"), 1);
                        ((NodePlayer) gamePlayer).start();
                    }
                };
                playerTasks.add(join.run());
            }
        }

        // Update player's inventory
        reopenPlayerInventory();
        // Update team menu
        ((NodeGame) MapNodes.getCurrentGame()).updateTeamChooserMenu();
    }

    /** Reopen player's inventory when they switch teams */
    public void reopenPlayerInventory() {
        if (inventory != null) {
            List<HumanEntity> viewers = inventory.getViewers();
            viewers.forEach(HumanEntity::closeInventory);
            inventory = Bukkit.createInventory(null, INV_SIZE, getPlayerColor());
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
            items[0] = pinv.getHelmet();
            items[1] = pinv.getChestplate();
            items[2] = pinv.getLeggings();
            items[3] = pinv.getBoots();

            // Health and Food
            items[8] = player.isDead() ? new ItemStack(Material.AIR) : getHunger();
            items[7] = player.isDead() ? new ItemStack(Material.AIR) : getHealth();

            // Items
            for (int i = 0; i < 36; i++) {
                // Hot Bar
                if (i < 9) {
                    boolean empty = pinv.getItem(i) == null;
                    items[(45-9) + i] = empty ? new ItemStack(Material.AIR) : pinv.getItem(i);
                }
                // Backpack
                else {
                    boolean empty = pinv.getItem(i) == null;
                    items[i] = empty ? new ItemStack(Material.AIR) : pinv.getItem(i);
                }
            }

            inventory.setContents(items);
        }, 5L));
    }

    /** Get the heal for the player. */
    private ItemStack getHealth() {
        int health = (int) player.getHealth();

        ItemStack level =  new ItemStack(Material.SPECKLED_MELON, health);
        ItemMeta meta = level.getItemMeta();
        meta.setDisplayName(MessageUtil.message(Msg.locale(player, "inv.health")));
        level.setItemMeta(meta);

        return level;
    }

    /** Get the hunger for the player. */
    private ItemStack getHunger() {
        int hunger = player.getFoodLevel();

        ItemStack level =  new ItemStack(Material.COOKED_BEEF, hunger);
        ItemMeta meta = level.getItemMeta();
        meta.setDisplayName(MessageUtil.message(Msg.locale(player, "inv.hunger")));
        level.setItemMeta(meta);

        return level;
    }

    /** Send a message with out grabing the player's instance first */
    public void sendMessage(String message, Object... args) {
        player.sendMessage(MessageUtil.message(message, args));
    }

    /** Get the player's color according to the team */
    public String getPlayerColor() {
        return MessageUtil.replaceColors(team.getColor() + player.getName());
    }
}
