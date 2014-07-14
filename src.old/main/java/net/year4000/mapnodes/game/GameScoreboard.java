package net.year4000.mapnodes.game;

import com.ewized.utilities.bukkit.util.MessageUtil;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.*;

@Data
@SuppressWarnings("unused")
public class GameScoreboard {
    /** ScoreboardManager, this allows us to have scoreboards. */
    @Getter(AccessLevel.PRIVATE)
    private final ScoreboardFactory sbm = Bukkit.getScoreboardManager();
    /** The score board for this game. */
    @Setter(AccessLevel.PRIVATE)
    private Scoreboard scoreboard;
    /** The sidebar objective. */
    private Objective sidebar;
    /** The objective bellow the player's name. */
    private Objective name;
    /** The objective for the tab list. */
    private Objective list;

    public GameScoreboard(GameMap map) {
        setScoreboard(getSbm().getNewScoreboard());

        // Sidebar
        setSidebar(getScoreboard().registerNewObjective("sidebar", "dummy"));
        getSidebar().setDisplayName(MessageUtil.replaceColors("&b" + map.getName()));
        getSidebar().setDisplaySlot(DisplaySlot.SIDEBAR);

        // Name
        //setName(getScoreboard().registerNewObjective("name","dummy"));
        //getName().setDisplaySlot(DisplaySlot.BELOW_NAME);

        // Player List
        setList(getScoreboard().registerNewObjective("list","dummy"));
        getList().setDisplaySlot(DisplaySlot.PLAYER_LIST);
    }

    /** Get a score for the name. */
    public Score getNameScore(String name) {
        String formatted = MessageUtil.replaceColors(name);
        return getName().getScore(formatted);
    }

    /** Get a score for the list. */
    public Score getListScore(String name) {
        String formatted = MessageUtil.replaceColors(name);
        return getList().getScore(formatted);
    }

    /** Get a score for the sidebar. */
    public Score getSidebarScore(String name) {
        String formatted = MessageUtil.replaceColors(name);
        return getSidebar().getScore(formatted);
    }

}
