package net.year4000.mapnodes.game.scoreboard;

import com.google.common.base.Splitter;
import net.year4000.mapnodes.utils.Common;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SidebarManager {
    int blankCounter = 1;
    private List<String> staticScores = new ArrayList<>();
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
            line += " ";
        }

        staticScores.add(line);
        return this;
    }

    /** Add a line */
    public SidebarManager addLine(String line, int number) {
        dynamicScores.add(new Object[]{line, number});
        return this;
    }

    /** Creates a team to allow for 48 chars and return the short */
    private String buildTeam(Scoreboard scoreboard, String name) {
        name = MessageUtil.replaceColors(Common.truncate(name, 48));

        if (name.length() <= 16) {
            return name;
        }

        String result;
        Team team = scoreboard.registerNewTeam(Common.truncate("txt:" + scoreboard.getTeams().size(), 16));
        Iterator<String> part = Splitter.fixedLength(16).split(name).iterator();
        team.setPrefix(part.next());
        result = part.next();

        if (name.length() > 32) {
            team.setPrefix(part.next());
        }

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
                // Apply true score a tick later
                SchedulerUtil.runSync(() -> {
                    try {
                        objective.getScore(scoreId).setScore(scoreInput);
                    } catch (IllegalStateException e) {
                        // MapNodesPlugin.debug(e, true);
                    }
                }, 2L);
            }
            // Handle score normally
            else {
                objective.getScore(scoreId).setScore(scoreInput);
            }
        }

        return objective;
    }
}