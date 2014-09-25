package net.year4000.mapnodes.api.game;

import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.utils.Operations;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public interface GameManager {
    /**
     * Get the info of the map from the json settings.
     * @return The map settings.
     */
    public GameMap getMap();

    /**
     * Get the map config settings.
     * @return The map config.
     */
    public GameConfig getConfig();

    public Map<String, GameKit> getKits();

    public Map<String, GameTeam> getTeams();

    public Map<String, GameRegion> getRegions();

    public Stream<GamePlayer> getPlayers();

    public Stream<GamePlayer> getPlaying();

    public Stream<GamePlayer> getSpectating();

    public Stream<GamePlayer> getEntering();

    public Set<GameMode> getGameModes();

    public GamePlayer getPlayer(Player player);

    public int getMaxPlayers();

    public GameStage getStage();

    public String defaultLocale(String key);

    public String locale(Locale locale, String key);

    public String locale(String locale, String key);

    public void addStartControl(Operations operation);

    public void addDynamicGoal(String id, String display, int score);

    public void addStaticGoal(String id, String display);

    public void openTeamChooserMenu(GamePlayer player);

    public Stream<GameTeam> getPlayingTeams();

}
