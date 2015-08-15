package net.year4000.mapnodes.games;

import com.google.common.collect.Maps;
import net.year4000.mapnodes.GameModeTemplate;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.events.game.GameLoadEvent;
import net.year4000.mapnodes.api.events.game.GameStartEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerJoinEvent;
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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
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
    private Iterator<BuildersConfig.Plot> avaiablePlots;
    private BukkitTask gameClock;

    /** The Current theme of the game */
    private Themes themes;
    private Map<Locale, String> theme;

    /** Get the translations of the current theme */
    public String getTheme(GamePlayer player) {
        checkNotNull(player);
        checkState(themes != null && theme != null, Msg.util("error.game.builders.theme_not_loaded"));

        return themes.translateTheme(theme, player.getLocale());
    }

    @EventHandler
    public void onGameLoad(GameLoadEvent event) {
        config = this.<BuildersConfig>getConfig();
        avaiablePlots = config.getPlots().iterator();

        // Load the theme for this game
        themes = Themes.get();
        theme = themes.randomTheme();

        GameTeam team = event.getGame().getPlayingTeams().collect(Collectors.toList()).iterator().next();
        team.setSize(config.getPlots().size());
    }

    @EventHandler
    public void onGameStart(GameStartEvent event) {
        stage = BuilderStage.BUILDING;

        gameClock = new Clocker(5, TimeUnit.MINUTES) {
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
                stage = BuilderStage.VOTING;
                // todo trigger voting
                MapNodes.getCurrentGame().stop();
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
    public void onPlayerJoin(GamePlayerJoinEvent event) {
        if (MapNodes.getCurrentGame().getStage().isPlaying()) {
            event.setMenu(false);
        }
    }

    @EventHandler
    public void onPlayerStart(GamePlayerStartEvent event) {
        checkState(avaiablePlots.hasNext(), "error.game.builders.not_enuf_plots");

        GamePlayer gamePlayer = event.getPlayer();
        PlayerPlot plot = new PlayerPlot(gamePlayer, avaiablePlots.next());
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
        Vector vector = event.getBlockPlaced().getLocation().toVector();
        Vector floor = plot.getPlot().getInnerMin();
        floor.setY(plot.getY());

        if (!vector.isInAABB(floor, plot.getPlot().getInnerMax())) {
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
        Vector floor = plot.getPlot().getInnerMin();
        floor.setY(plot.getY());

        if (!vector.isInAABB(floor, plot.getPlot().getInnerMax())) {
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
        Vector limit = plot.getPlot().getOuterMax();
        limit.setY(plot.getY() + config.getHeight());

        if (!vector.isInAABB(plot.getPlot().getMin(), limit)) {
            gamePlayer.sendMessage(Msg.NOTICE + Msg.locale(gamePlayer, "region.exit.room"));

            event.setTo(plot.teleportToPlot(gamePlayer));
        }
    }
}
