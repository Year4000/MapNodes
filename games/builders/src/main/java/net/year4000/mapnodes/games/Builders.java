/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.games;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.year4000.mapnodes.GameModeTemplate;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.events.game.GameLoadEvent;
import net.year4000.mapnodes.api.events.game.GameStartEvent;
import net.year4000.mapnodes.api.events.game.GameStopEvent;
import net.year4000.mapnodes.api.events.player.*;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.GameTeam;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.api.utils.Spectator;
import net.year4000.mapnodes.clocks.Clocker;
import net.year4000.mapnodes.game.NodeGame;
import net.year4000.mapnodes.game.NodePlayer;
import net.year4000.mapnodes.game.scoreboard.SidebarManager;
import net.year4000.mapnodes.games.gui.PlotManager;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Common;
import net.year4000.mapnodes.utils.MathUtil;
import net.year4000.mapnodes.utils.PacketHacks;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.utilities.MessageUtil;
import net.year4000.utilities.TimeUtil;
import net.year4000.utilities.bukkit.BukkitUtil;
import net.year4000.utilities.bukkit.FunEffectsUtil;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

@GameModeInfo(
    name = "Builders",
    version = "1.0",
    config = BuildersConfig.class
)
public class Builders extends GameModeTemplate implements GameMode {
    private BuildersConfig config;
    private BuilderStage stage = BuilderStage.PRE_GAME;
    private Map<GamePlayer, PlayerPlot> plots = Maps.newHashMap();
    private Iterator<BuildersConfig.Plot> availablePlots;
    private BukkitTask gameClock;
    private Iterator<PlayerPlot> voting;
    private PlayerPlot currentVoting;
    List<PlotManager> guis = Lists.newArrayList();
    private static final ItemStack itemBase;
    static {
        itemBase = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = itemBase.getItemMeta();
        meta.spigot().setUnbreakable(true);
        itemBase.setItemMeta(meta);
    }

    /** The Current theme of the game */
    private Themes themes;
    private Map<Locale, String> theme;

    /** Get the translations of the current theme */
    public String getTheme(GamePlayer player) {
        checkNotNull(player);
        checkState(themes != null && theme != null, Msg.util("error.game.builders.theme_not_loaded"));

        return themes.translateTheme(theme, player.getLocale());
    }

    /** Process the plots */
    public void processVoting() {
        stage = BuilderStage.VOTING;

        // Set up iterator
        if (voting == null) {
            voting = plots.values()
                .stream()
                .filter(plot -> !plot.isForfeited())
                .iterator();
        }

        // No more plots end game
        if (!voting.hasNext()) {
            endGame();
            return;
        }

        PlayerPlot plot = currentVoting = voting.next();

        MapNodes.getCurrentGame().getPlayers().forEach(gamePlayer -> {
            gamePlayer.getPlayer().setGameMode(org.bukkit.GameMode.ADVENTURE);
            gamePlayer.getPlayer().closeInventory();
            VoteType.setInventory(gamePlayer);
            plot.teleportToPlot(gamePlayer);
            VoteType voteType = plot.getOwner().equals(gamePlayer.getPlayerColor()) ? VoteType.INVALID : VoteType.NO_VOTE;
            setVoteSidebar(gamePlayer, plot.getPlayer(), voteType);
        });

        // Set the
        gameClock = new Clocker(MapNodesPlugin.getInst().isDebug() ? 30 : 15, TimeUnit.SECONDS) {
            @Override
            public void runTock(int position) {
                if (position % 20 != 0) return;

                int currentTime = MathUtil.sec(position);
                String color = Common.chatColorNumber(position, getTime());
                String clock = color + (new TimeUtil(currentTime, TimeUnit.SECONDS)).prettyOutput("&7:" + color);

                MapNodes.getCurrentGame().getPlaying().forEach(gamePlayer -> {
                    Player player = gamePlayer.getPlayer();
                    String message = Msg.locale(gamePlayer, "builders.voting", clock);
                    PacketHacks.sendActionBarMessage(player, MessageUtil.replaceColors(message));

                    // Display on title at specific integers
                    if (currentTime <= 5) {
                        PacketHacks.setTitle(player, clock, "", 5, 20, 0);
                        FunEffectsUtil.playSound(player, Sound.CHICKEN_EGG_POP);
                    }
                });
            }

            @Override
            public void runLast(int position) {
                processVoting();
            }
        }.run();
        MapNodes.getCurrentGame().addTask(gameClock);
    }

    /** Calculate the winner and show scores */
    public void endGame() {
        stage = BuilderStage.RESULTS;

        List<PlayerPlot> voting = plots.values()
            .stream()
            .filter(plot -> !plot.isForfeited())
            .sorted()
            .collect(Collectors.toList());

        PlayerPlot winningPlot = voting.get(0);
        SidebarManager sidebar = new SidebarManager();

        for (PlayerPlot plot : voting) {
            sidebar.addLine(plot.getOwner(), plot.calculateScore());
        }

        // Set sidebar
        MapNodes.getCurrentGame().getPlayers().forEach(player -> {
            winningPlot.teleportToPlot(player);
            winningPlot.fireworks();
            ((NodeGame) MapNodes.getCurrentGame()).getScoreboardFactory().setCustomSidebar((NodePlayer) player, sidebar);

            // Launch firework at player position
            Location location = player.getPlayer().getLocation().clone().add(Common.randomOffset());
            Firework firework = MapNodes.getCurrentWorld().spawn(location, Firework.class);
            FireworkEffect effect = FireworkEffect.builder()
                .withColor((Color) BukkitUtil.COLOR_MAP.keySet().toArray()[new Random().nextInt(BukkitUtil.COLOR_MAP.keySet().size())])
                .withColor((Color) BukkitUtil.COLOR_MAP.keySet().toArray()[new Random().nextInt(BukkitUtil.COLOR_MAP.keySet().size())])
                .withColor((Color) BukkitUtil.COLOR_MAP.keySet().toArray()[new Random().nextInt(BukkitUtil.COLOR_MAP.keySet().size())])
                .withColor((Color) BukkitUtil.COLOR_MAP.keySet().toArray()[new Random().nextInt(BukkitUtil.COLOR_MAP.keySet().size())])
                .withFade((Color) BukkitUtil.COLOR_MAP.keySet().toArray()[new Random().nextInt(BukkitUtil.COLOR_MAP.keySet().size())])
                .withFade((Color) BukkitUtil.COLOR_MAP.keySet().toArray()[new Random().nextInt(BukkitUtil.COLOR_MAP.keySet().size())])
                .withFade((Color) BukkitUtil.COLOR_MAP.keySet().toArray()[new Random().nextInt(BukkitUtil.COLOR_MAP.keySet().size())])
                .withFade((Color) BukkitUtil.COLOR_MAP.keySet().toArray()[new Random().nextInt(BukkitUtil.COLOR_MAP.keySet().size())])
                .with(FireworkEffect.Type.BURST)
                .build();
            FireworkMeta meta = firework.getFireworkMeta();
            meta.clearEffects();
            meta.addEffect(effect);
            meta.setPower(0);
            firework.setFireworkMeta(meta);
        });

        // After 5 secs
        MapNodes.getCurrentGame().addTask(SchedulerUtil.runSync(() -> {
            GamePlayerWinEvent win = new GamePlayerWinEvent(MapNodes.getCurrentGame(), winningPlot.getPlayer());
            List<String> top = Lists.newArrayList();

            for (int i = 0; i < 3; i++) {
                if (i < voting.size()) {
                    PlayerPlot plot = voting.get(i);
                    top.add(plot.getOwner() + "&7: " + plot.calculateScore());
                }
            }

            win.setMessage(top);
            win.call();
        }, 5 * 20L));
    }

    /** Set the sidebar for vote */
    private void setVoteSidebar(GamePlayer gamePlayer, GamePlayer plotOwner, VoteType voteType) {
        SidebarManager sidebar = new SidebarManager();
        sidebar.addBlank();
        sidebar.addLine(Msg.locale(gamePlayer, "builders.theme", getTheme(gamePlayer)));
        sidebar.addLine(Msg.locale(gamePlayer, "builders.owner", plotOwner.getPlayerColor()));
        sidebar.addLine(Msg.locale(gamePlayer, "builders.vote", voteType.voteName(gamePlayer)));
        sidebar.addBlank();
        sidebar.addLine(" &bwww&3.&byear4000&3.&bnet ");

        ((NodeGame) MapNodes.getCurrentGame()).getScoreboardFactory().setCustomSidebar((NodePlayer) gamePlayer, sidebar);
    }

    @EventHandler
    public void onGameGameStop(GameStopEvent event) {
        // Unregister menus
        guis.forEach(MapNodes.getGui()::unregisterMenu);
    }

    @EventHandler
    public void onGameLoad(GameLoadEvent event) {
        config = this.<BuildersConfig>getConfig();
        availablePlots = config.getPlots().iterator();

        // Load the theme for this game
        themes = Themes.get();
        theme = themes.randomTheme();

        GameTeam team = event.getGame().getPlayingTeams().collect(Collectors.toList()).iterator().next();
        team.setSize(config.getPlots().size());
    }

    @EventHandler
    public void onGameStart(GameStartEvent event) {
        stage = BuilderStage.BUILDING;

        // 2:30 dev = 150 | 5:30 production = 330
        gameClock = new Clocker(MapNodesPlugin.getInst().isDebug() ? 150 : 330, TimeUnit.SECONDS) {
            @Override
            public void runTock(int position) {
                if (position % 20 != 0) return;

                int currentTime = MathUtil.sec(position);
                String color = Common.chatColorNumber(position, getTime());
                String clock = color + (new TimeUtil(currentTime, TimeUnit.SECONDS)).prettyOutput("&7:" + color);

                MapNodes.getCurrentGame().getPlaying().forEach(gamePlayer -> {
                    Player player = gamePlayer.getPlayer();
                    String message = Msg.locale(gamePlayer, "builders.time", clock);
                    PacketHacks.sendActionBarMessage(player, MessageUtil.replaceColors(message));

                    // Display on title at specific integers
                    if (currentTime % 60 >= 58 || currentTime % 60 == 0 || currentTime <= 10) {
                        PacketHacks.setTitle(player, clock, "", 0, 20, 5);
                        FunEffectsUtil.playSound(player, Sound.CHICKEN_EGG_POP);
                    }

                    // Make sure the player has the plot stick
                    ItemStack item = itemBase.clone();
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(Msg.locale(gamePlayer, "builders.plot.stick"));
                    item.setItemMeta(meta);
                    gamePlayer.getPlayer().getInventory().setItem(8, item);
                });
            }

            @Override
            public void runLast(int position) {
                processVoting();
            }
        }.run();
        event.getGame().addTask(gameClock);

        // Let the spectator know the theme
        event.getGame().addTask(SchedulerUtil.repeatSync(() -> {
            MapNodes.getCurrentGame().getSpectating().forEach(gamePlayer -> {
                Player player = gamePlayer.getPlayer();
                String message = Msg.locale(gamePlayer, "builders.theme", getTheme(gamePlayer));
                PacketHacks.sendActionBarMessage(player, MessageUtil.replaceColors(message));
            });
        }, 20L));
    }

    /** Don't allow players to select teams when the game started */
    @EventHandler
    public void onTeamSelect(GamePlayerJoinTeamEvent event) {
        if (event.getTo() instanceof Spectator) return;

        if (MapNodes.getCurrentGame().getStage().isPlaying()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Msg.NOTICE + Msg.locale(event.getPlayer(), "builders.join.playing"));
        }
    }

    /** Disable menu when game started */
    @EventHandler
    public void onSpectatorJoin(GamePlayerJoinSpectatorEvent event) {
        if (MapNodes.getCurrentGame().getStage().isPlaying()) {
            if (plots.containsKey(event.getPlayer())) {
                plots.get(event.getPlayer()).setForfeited(true);
            }
        }
    }

    /** Disable menu when game started */
    @EventHandler
    public void onPlayerJoin(GamePlayerJoinEvent event) {
        if (MapNodes.getCurrentGame().getStage().isPlaying()) {
            event.setMenu(false);
        }
    }

    @EventHandler
    public void onPlayerStart(GamePlayerStartEvent event) {
        checkState(availablePlots.hasNext(), "error.game.builders.not_enuf_plots");

        GamePlayer gamePlayer = event.getPlayer();
        PlayerPlot plot = new PlayerPlot(this, gamePlayer, availablePlots.next());
        plots.put(gamePlayer, plot);
        event.setImmortal(false);
        event.setSpawn(plot.teleportPlotLocation());
        String theme = getTheme(gamePlayer);

        // Set the sidebar after the player has fully joined
        event.addPostEvent(() -> {
            Player player = gamePlayer.getPlayer();
            player.setGameMode(org.bukkit.GameMode.CREATIVE);
            player.setAllowFlight(true);
            player.setFlying(true);

            gamePlayer.sendMessage(Msg.locale(gamePlayer, "builders.theme", theme));
            SidebarManager sidebar = new SidebarManager();
            sidebar.addBlank();
            sidebar.addLine(Msg.locale(gamePlayer, "builders.theme", theme));
            sidebar.addLine(Msg.locale(gamePlayer, "builders.owner", gamePlayer.getPlayerColor()));
            sidebar.addBlank();
            sidebar.addLine(" &bwww&3.&byear4000&3.&bnet ");

            ((NodeGame) MapNodes.getCurrentGame()).getScoreboardFactory().setCustomSidebar((NodePlayer) gamePlayer, sidebar);
        });
    }

    // Plots

    @EventHandler(ignoreCancelled = true)
    public void onPlayerBuild(BlockPlaceEvent event) {
        if (!MapNodes.getCurrentGame().getStage().isPlaying()) return;

        if (stage == BuilderStage.VOTING) {
            event.setCancelled(true);
            return;
        }

        GamePlayer gamePlayer = MapNodes.getCurrentGame().getPlayer(event.getPlayer());

        if (!gamePlayer.isPlaying()) return;

        PlayerPlot plot = gamePlayer.getPlayerData(PlayerPlot.class);
        Vector vector = event.getBlock().getLocation().toVector();

        if (!plot.getPlot().isInInnerPlot(vector, plot.getY(), plot.getPlot().getMax().getBlockY())) {
            gamePlayer.sendMessage(Msg.NOTICE + Msg.locale(gamePlayer, "region.build.region"));
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerBuild(BlockBreakEvent event) {
        if (!MapNodes.getCurrentGame().getStage().isPlaying()) return;

        if (stage == BuilderStage.VOTING) {
            event.setCancelled(true);
            return;
        }

        GamePlayer gamePlayer = MapNodes.getCurrentGame().getPlayer(event.getPlayer());

        if (!gamePlayer.isPlaying()) return;

        PlayerPlot plot = gamePlayer.getPlayerData(PlayerPlot.class);
        Vector vector = event.getBlock().getLocation().toVector();

        if (!plot.getPlot().isInInnerPlot(vector, plot.getY(), plot.getPlot().getMax().getBlockY())) {
            gamePlayer.sendMessage(Msg.NOTICE + Msg.locale(gamePlayer, "region.destroy.region"));
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerBuild(PlayerMoveEvent event) {
        if (!MapNodes.getCurrentGame().getStage().isPlaying()) return;

        // Only run when moving full block
        if (event.getFrom().toVector().toBlockVector().equals(event.getTo().toVector().toBlockVector())) return;

        GamePlayer gamePlayer = MapNodes.getCurrentGame().getPlayer(event.getPlayer());
        Vector vector = event.getTo().toVector();
        PlayerPlot plot;

        if (!gamePlayer.isPlaying()) return;

        // When voting say in voters plot
        if (stage == BuilderStage.VOTING) {
            plot = currentVoting.getPlayer().getPlayerData(PlayerPlot.class);
        }
        else {
            plot = gamePlayer.getPlayerData(PlayerPlot.class);
        }

        if (!plot.getPlot().isInPlot(vector, plot.getY(), plot.getY() + config.getHeight())) {
            gamePlayer.sendMessage(Msg.NOTICE + Msg.locale(gamePlayer, "region.exit.room"));

            // Try to use from, if fails send to plot
            if (plot.getPlot().isInPlot(event.getFrom().toVector())) {
                event.setTo(event.getFrom().clone());
            }
            else {
                event.setTo(plot.teleportPlotLocation());
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (stage == BuilderStage.VOTING) {
            event.setCancelled(true);
        }
    }

    // Voting

    @EventHandler
    public void onVoting(PlayerInteractEvent event) {
        if (stage != BuilderStage.VOTING || currentVoting == null) return;

        GamePlayer gamePlayer = MapNodes.getCurrentGame().getPlayer(event.getPlayer());

        if (!gamePlayer.isPlaying()) return;

        Optional<ItemStack> holding = Optional.ofNullable(event.getItem());

        if (holding.isPresent() && !currentVoting.getOwner().equals(gamePlayer.getPlayerColor())) {
            ItemStack item = holding.get();

            try {
                VoteType voteType = VoteType.getVoteType(item, gamePlayer);
                voteType.playSound(gamePlayer);
                currentVoting.getVotes().put(gamePlayer, voteType);
                gamePlayer.sendMessage(Msg.locale(gamePlayer, "builders.vote.selected"));
                setVoteSidebar(gamePlayer, currentVoting.getPlayer(), voteType);
            }
            catch (IllegalArgumentException | NullPointerException error) {
                // Invalid
            }
        }
        else if (holding.isPresent()) {
            gamePlayer.sendMessage(Msg.locale(gamePlayer, "builders.vote.owner"));
            VoteType.INVALID.playSound(gamePlayer);
        }
    }

    // Misc Plot Events

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrop(EntitySpawnEvent event) {
        if (event.getEntityType() == EntityType.ARMOR_STAND) {
            event.setCancelled(false);
        }

        // Disable items on floor
        if (event.getEntityType() == EntityType.DROPPED_ITEM) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            GamePlayer gamePlayer = MapNodes.getCurrentGame().getPlayer((Player) event.getWhoClicked());

            /** Disable inventory during voting */
            if (gamePlayer.isPlaying() && stage == BuilderStage.VOTING) {
                event.setCancelled(true);
            }

            /** Can not change armor */
            if (gamePlayer.isPlaying() && event.getSlotType() == InventoryType.SlotType.ARMOR) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlotStick(PlayerInteractEvent event) {
        boolean rightBlock = event.getAction() == Action.RIGHT_CLICK_BLOCK;
        boolean rightAir = event.getAction() == Action.RIGHT_CLICK_AIR;

        if (rightBlock || rightAir) {
            ItemStack plotStick = event.getPlayer().getItemInHand();

            if (plotStick.getType() == itemBase.getType() && plotStick.getItemMeta().spigot().isUnbreakable()) {
                GamePlayer gamePlayer = MapNodes.getCurrentGame().getPlayer(event.getPlayer());
                PlayerPlot plot = gamePlayer.getPlayerData(PlayerPlot.class);
                plot.getPlotManager().openInventory(event.getPlayer());
            }
        }
    }
}
