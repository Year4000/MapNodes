package net.year4000.mapnodes.games;

import com.google.common.collect.Maps;
import net.year4000.mapnodes.GameModeTemplate;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.events.game.GameLoadEvent;
import net.year4000.mapnodes.api.events.game.GameStartEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerJoinEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerJoinSpectatorEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerJoinTeamEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerStartEvent;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.GameTeam;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.api.utils.Spectator;
import net.year4000.mapnodes.clocks.Clocker;
import net.year4000.mapnodes.game.NodeGame;
import net.year4000.mapnodes.game.NodePlayer;
import net.year4000.mapnodes.game.scoreboard.SidebarManager;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Common;
import net.year4000.mapnodes.utils.MathUtil;
import net.year4000.mapnodes.utils.PacketHacks;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.utilities.MessageUtil;
import net.year4000.utilities.TimeUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
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

    /** The Current theme of the game */
    private Themes themes;
    private Map<Locale, String> theme;

    /** Get the translations of the current theme */
    public String getTheme(GamePlayer player) {
        checkNotNull(player);
        checkState(themes != null && theme != null, Msg.util("error.game.builders.theme_not_loaded"));

        return themes.translateTheme(theme, player.getLocale());
    }

    public void processVoting() {
        stage = BuilderStage.VOTING;

        // Set up iterator
        if (voting == null) {
            voting = plots.values()
                .stream()
                .filter(plot -> !plot.isForfeited())
                .iterator();
        }

        if (!voting.hasNext()) {
            // todo find the winners
            MapNodes.getCurrentGame().getPlayers().forEach(player -> {
                Iterator<PlayerPlot> voting = plots.values()
                    .stream()
                    .filter(plot -> !plot.isForfeited())
                    .sorted()
                    .iterator();

                while (voting.hasNext()) {
                    PlayerPlot plot = voting.next();
                    player.sendMessage(plot.getOwner() + "&7: &e" + plot.calculateScore());
                }
            });
            MapNodes.getCurrentGame().stop();
            return;
        }

        PlayerPlot plot = currentVoting = voting.next();

        MapNodes.getCurrentGame().getPlayers().forEach(gamePlayer -> {
            VoteType.setInventory(gamePlayer);
            plot.teleportToPlot(gamePlayer);
            plot.addPlotEffects(gamePlayer);

            SidebarManager sidebar = new SidebarManager();
            sidebar.addBlank();
            sidebar.addLine(Msg.locale(gamePlayer, "builders.theme", getTheme(gamePlayer)));
            sidebar.addBlank();
            sidebar.addLine(Msg.locale(gamePlayer, "builders.owner", gamePlayer.getPlayerColor()));
            sidebar.addBlank();
            if (plot.getOwner().equals(gamePlayer.getPlayerColor())) {
                sidebar.addLine(Msg.locale(gamePlayer, "builders.vote", VoteType.INVALID.voteName(gamePlayer)));
            }
            else {
                sidebar.addLine(Msg.locale(gamePlayer, "builders.vote", VoteType.NO_VOTE.voteName(gamePlayer)));
            }
            sidebar.addBlank();
            sidebar.addLine(" &bwww&3.&byear4000&3.&bnet ");

            ((NodeGame) MapNodes.getCurrentGame()).getScoreboardFactory().setCustomSidebar((NodePlayer) gamePlayer, sidebar);

        });

        // Set the
        gameClock = new Clocker(MapNodesPlugin.getInst().isDebug() ? 30 : 15, TimeUnit.SECONDS) {
            @Override
            public void runTock(int position) {
                int currentTime = MathUtil.sec(position);
                String color = Common.chatColorNumber(position, getTime());
                String clock = color + (new TimeUtil(currentTime, TimeUnit.SECONDS)).prettyOutput("&7:" + color);

                MapNodes.getCurrentGame().getPlaying().forEach(gamePlayer -> {
                    Player player = gamePlayer.getPlayer();
                    String message = Msg.locale(gamePlayer, "builders.voting", clock);
                    PacketHacks.sendActionBarMessage(player, MessageUtil.replaceColors(message));
                });
            }

            @Override
            public void runLast(int position) {
                processVoting();
            }
        }.run();
        MapNodes.getCurrentGame().addTask(gameClock);
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

        gameClock = new Clocker(MapNodesPlugin.getInst().isDebug() ? 1 : 5, TimeUnit.MINUTES) {
            @Override
            public void runTock(int position) {
                int currentTime = MathUtil.sec(position);
                String color = Common.chatColorNumber(position, getTime());
                String clock = color + (new TimeUtil(currentTime, TimeUnit.SECONDS)).prettyOutput("&7:" + color);

                MapNodes.getCurrentGame().getPlaying().forEach(gamePlayer -> {
                    Player player = gamePlayer.getPlayer();
                    String message = Msg.locale(gamePlayer, "builders.time", clock);
                    PacketHacks.sendActionBarMessage(player, MessageUtil.replaceColors(message));
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
        PlayerPlot plot = new PlayerPlot(gamePlayer, availablePlots.next());
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
            sidebar.addBlank();
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

        if (!gamePlayer.isPlaying()) return;

        PlayerPlot plot = gamePlayer.getPlayerData(PlayerPlot.class);
        Vector vector = event.getTo().toVector();

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

                SidebarManager sidebar = new SidebarManager();
                sidebar.addBlank();
                sidebar.addLine(Msg.locale(gamePlayer, "builders.theme", getTheme(gamePlayer)));
                sidebar.addBlank();
                sidebar.addLine(Msg.locale(gamePlayer, "builders.owner", gamePlayer.getPlayerColor()));
                sidebar.addBlank();
                sidebar.addLine(Msg.locale(gamePlayer, "builders.vote", voteType.voteName(gamePlayer)));
                sidebar.addBlank();
                sidebar.addLine(" &bwww&3.&byear4000&3.&bnet ");

                ((NodeGame) MapNodes.getCurrentGame()).getScoreboardFactory().setCustomSidebar((NodePlayer) gamePlayer, sidebar);
            }
            catch (IllegalArgumentException | NullPointerException error) {
                // Invalid
            }
        }
        else {
            gamePlayer.sendMessage(Msg.locale(gamePlayer, "builders.vote.owner"));
            VoteType.INVALID.playSound(gamePlayer);
        }
    }
}
