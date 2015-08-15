package net.year4000.mapnodes.games;

import com.google.common.collect.Maps;
import net.year4000.mapnodes.GameModeTemplate;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.events.game.GameLoadEvent;
import net.year4000.mapnodes.api.events.game.GameStartEvent;
import net.year4000.mapnodes.api.events.game.GameWinEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerStartEvent;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.clocks.Clocker;
import net.year4000.mapnodes.game.NodeGame;
import net.year4000.mapnodes.game.NodePlayer;
import net.year4000.mapnodes.game.scoreboard.SidebarManager;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.MathUtil;
import net.year4000.mapnodes.utils.PacketHacks;
import net.year4000.utilities.MessageUtil;
import net.year4000.utilities.TimeUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitTask;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    }

    @EventHandler
    public void onGameStart(GameStartEvent event) {
        stage = BuilderStage.BUILDING;

        gameClock = new Clocker(5, TimeUnit.MINUTES) {
            @Override
            public void runTock(int position) {
                MapNodes.getCurrentGame().getPlaying().forEach(gamePlayer -> {
                    Player player = gamePlayer.getPlayer();
                    String clock = (new TimeUtil(MathUtil.sec(position), TimeUnit.SECONDS)).prettyOutput("&7:&a");
                    String message = "&6Time Left &a" + clock;
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
    }

    @EventHandler
    public void onPlayerStart(GamePlayerStartEvent event) {
        checkState(avaiablePlots.hasNext(), "error.game.builders.not_enuf_plots");

        GamePlayer gamePlayer = event.getPlayer();
        PlayerPlot plot = new PlayerPlot(gamePlayer, avaiablePlots.next());
        plots.put(gamePlayer, plot);
        event.setSpawn(plot.teleportPlotLocation());
        String theme = getTheme(gamePlayer);

        // Set the sidebar after the player has fully joined
        event.addPostEvent(() -> {
            Player player = gamePlayer.getPlayer();
            player.setGameMode(org.bukkit.GameMode.CREATIVE);

            gamePlayer.sendMessage("&6Game Theme&7: &e" + theme);
            SidebarManager sidebar = new SidebarManager();
            sidebar.addBlank();
            sidebar.addLine("&6Theme&7: &e" + theme);
            sidebar.addBlank();
            sidebar.addLine("&6Owner&7: " + gamePlayer.getPlayerColor());
            ((NodeGame)MapNodes.getCurrentGame()).getScoreboardFactory().setCustomSidebar((NodePlayer) gamePlayer, sidebar);
        });
    }
}
