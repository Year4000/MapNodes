package net.year4000.mapnodes.api.game;

import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.game.NodeKit;
import net.year4000.mapnodes.game.NodeRegion;
import net.year4000.mapnodes.game.NodeTeam;
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

    public Map<String, NodeKit> getKits();

    public Map<String, NodeTeam> getTeams();

    public Map<String, NodeRegion> getRegions();

    public Stream<GamePlayer> getPlayers();

    public Stream<GamePlayer> getPlaying();

    public Stream<GamePlayer> getSpectating();

    public Stream<GamePlayer> getEntering();

    public Set<GameMode> getGameModes();

    public GamePlayer getPlayer(Player player);

    public int getMaxPlayers();

    public GameStage getStage();

    public String locale(Locale locale, String key);

    public String locale(String locale, String key);
}
