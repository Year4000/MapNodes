package net.year4000.mapnodes.api.game;

import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.scoreboard.GameSidebarGoal;
import net.year4000.mapnodes.api.utils.Operations;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public interface GameManager {
    /** Get the info of the map from the json settings */
    public GameMap getMap();

    /** Get the map config settings */
    public GameConfig getConfig();

    public Map<String, GameKit> getKits();

    public Map<String, GameTeam> getTeams();

    public Map<String, GameClass> getClasses();

    public Map<String, GameSidebarGoal> getSidebarGoals();

    public Stream<GameTeam> getPlayingTeams();

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

    public void addStartTime(int time);

    public void loadTeam(String id, GameTeam team);

    public void loadKit(String id, GameKit kit);

    public void loadClass(String id, GameClass team);

    public void loadRegion(String id, GameRegion team);

    public GameSidebarGoal addDynamicGoal(String id, String display, int score);

    public GameSidebarGoal addDynamicGoal(String id, String owner, String display, int score);

    public GameSidebarGoal addStaticGoal(String id, String display);

    public GameSidebarGoal addStaticGoal(String id, String owner, String display);

    public void openClassKitChooserMenu(GamePlayer player);

    public void openTeamChooserMenu(GamePlayer player);

    public GameClass getClassKit(String name);

    public int getRealMaxCount();

    public String getTabHeader(GamePlayer player);

    public String getTabHeader();

    public String getTabFooter();

    public void join(Player player);

    public void quit(Player player);

    public boolean shouldStart();

    public List<String> getBookPages(Player player);

    public long getStartTime();

    public List<Operations> getStartControls();

    public void stop();
}
