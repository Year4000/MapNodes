package net.year4000.mapnodes.games;

import com.google.common.collect.Maps;
import net.year4000.mapnodes.GameModeTemplate;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.events.game.GameLoadEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerStartEvent;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.game.NodeGame;
import net.year4000.mapnodes.game.NodePlayer;
import net.year4000.mapnodes.game.scoreboard.SidebarManager;
import net.year4000.mapnodes.messages.Msg;
import org.bukkit.event.EventHandler;

import java.util.Locale;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

@GameModeInfo(
    name = "Builders",
    version = "1.0",
    config = BuildersConfig.class
)
public class Builders extends GameModeTemplate implements GameMode {
    private BuildersConfig config;

    /** The current game stage */
    private BuilderStage stage = BuilderStage.PRE_GAME;

    /** The player plots */
    private Map<GamePlayer, PlayerPlot> plots = Maps.newHashMap();

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

        // Load the theme for this game
        themes = Themes.get();
        theme = themes.randomTheme();

        MapNodesPlugin.log(themes.toString());
        MapNodesPlugin.log(config.toString());
    }

    @EventHandler
    public void onPlayerStart(GamePlayerStartEvent event) {
        GamePlayer gamePlayer = event.getPlayer();
        PlayerPlot plot = new PlayerPlot();
        plots.put(gamePlayer, plot);
        String theme = getTheme(gamePlayer);

        event.addPostEvent(() -> {
            gamePlayer.sendMessage("&6Game Theme&7: &e" + theme);
            SidebarManager sidebar = new SidebarManager();
            sidebar.addBlank();
            sidebar.addLine("&6Theme&7: &e" + theme);
            sidebar.addBlank();
            sidebar.addLine(gamePlayer.getPlayerColor());
            ((NodeGame)MapNodes.getCurrentGame()).getScoreboardFactory().setCustomSidebar((NodePlayer) gamePlayer, sidebar);
        });
    }
}
