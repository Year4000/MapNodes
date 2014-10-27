package net.year4000.mapnodes.game.scoreboard;

import com.google.common.base.Splitter;
import net.year4000.mapnodes.utils.Common;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.utilities.ChatColor;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public final class SidebarManager {
    private int blankCounter = 1;
    private List<String> staticScores = new ArrayList<>();
    private List<String> customTeams = new ArrayList<>();
    private List<Object[]> dynamicScores = new ArrayList<>();

    /** Add a blank line */
    public SidebarManager addBlank() {
        String line = "";

        for (int i = 0; i < blankCounter; i++) {
            line += " ";
        }

        blankCounter++;
        staticScores.add(line);

        return this;
    }

    /** Add a line */
    public SidebarManager addLine(String line) {
        // When scores are the same append a blank until the string is different.
        while (staticScores.contains(line)) {
            line += "&r";
        }

        staticScores.add(line);
        return this;
    }

    /** Add a line */
    public SidebarManager addLine(String line, int number) {
        dynamicScores.add(new Object[]{line, number});
        return this;
    }

    /** Creates a team to allow for 48 chars and return the display name */
    private String buildTeam(Scoreboard scoreboard, String name) {
        name = MessageUtil.replaceColors(Common.truncate(name, 48));

        if (name.length() <= 16) {
            return name;
        }

        Team team = scoreboard.registerNewTeam(Common.truncate("txt:" + scoreboard.getTeams().size(), 16));
        Iterator<String> part = Splitter.fixedLength(16).split(name).iterator();
        String prefix = part.next(), result = part.next(), nameHack, lastColor = "", lastFormat = "";
        int size = prefix.length();

        // Find and set last used color and color format
        for (int i = 0; i < size - 2; i++) {
            String key = prefix.substring(i, i + 2);

            if (ChatColor.COLOR_CHAR != key.charAt(0)) continue;

            // Match last color
            if (Pattern.matches("[0-9a-fA-F]", String.valueOf(key.charAt(1)))) {
                lastColor = key;
            }

            // Match last format
            if (Pattern.matches("[K-Ok-o]", String.valueOf(key.charAt(1)))) {
                lastFormat = key;
            }
        }

        while (customTeams.contains(result)) {
            result = lastColor + lastFormat + result;
        }

        customTeams.add(result);
        nameHack = result;

        // Append the suffix to the name hack
        if (part.hasNext()) {
            nameHack += part.next();
        }

        // Split the nameHack into the name and suffix
        nameHack = MessageUtil.replaceColors(Common.truncate(nameHack, 32));
        Iterator<String> suffix = Splitter.fixedLength(16).split(nameHack).iterator();
        result = suffix.next();

        if (suffix.hasNext()) {
            team.setSuffix(suffix.next());
        }

        team.setPrefix(prefix);
        team.add(result);
        return result;
    }

    public Objective buildSidebar(Scoreboard scoreboard, String title) {
        String id = String.valueOf(title.hashCode());

        // Unregister if exists
        if (scoreboard.getObjective(id) != null) {
            scoreboard.getObjective(id).unregister();
        }

        // Register it
        Objective objective = scoreboard.registerNewObjective(id, "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        scoreboard.getObjective(DisplaySlot.SIDEBAR).setDisplayName(Common.truncate(MessageUtil.replaceColors(title), 32));

        // Add static scores that are the order they are in the list
        for (int i = 0; i < staticScores.size(); i++) {
            Score score = objective.getScore(buildTeam(scoreboard, staticScores.get(i)));
            score.setScore(-(i + 1));
        }

        // Add dynamic scores that don't depend on statics
        for (Object[] lines : dynamicScores) {
            String scoreId = buildTeam(scoreboard, ((String) lines[0]));
            int scoreInput = (Integer) lines[1];

            // Set default score to fix Bukkit / Minecraft cant start with 0
            if (scoreInput == 0) {
                objective.getScore(scoreId).setScore(1);
                objective.getScore(scoreId).setScore(0);
            }
            // Handle score normally
            else {
                objective.getScore(scoreId).setScore(scoreInput);
            }
        }

        return objective;
    }
}