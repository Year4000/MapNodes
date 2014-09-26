package net.year4000.mapnodes.game.scoreboard;

import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
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

    public Objective buildSidebar(Scoreboard scoreboard, String title) {
        String id = String.valueOf(title.hashCode());

        // Unregister if exists
        if (scoreboard.getObjective(id) != null) {
            scoreboard.getObjective(id).unregister();
        }

        // Register it
        Objective objective = scoreboard.registerNewObjective(id, "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        scoreboard.getObjective(DisplaySlot.SIDEBAR).setDisplayName(MessageUtil.replaceColors(title));

        // Add static scores that are the order they are in the list
        for (int i = 0; i < staticScores.size(); i++) {
            Score score = objective.getScore(MessageUtil.replaceColors(staticScores.get(i)));
            score.setScore(-(i + 1));
        }

        // Add dynamic scores that don't depend on statics
        for (Object[] lines : dynamicScores) {
            Score score = objective.getScore(MessageUtil.replaceColors((String) lines[0]));
            int scoreInput = (Integer) lines[1];

            // Set default score to fix Bukkit / Minecraft cant start with 0
            if (scoreInput == 0) {
                score.setScore(1);
                // Apply true score a tick later
                SchedulerUtil.runSync(() -> score.setScore(scoreInput), 1L);
            }
            // Handle score normally
            else {
                score.setScore(scoreInput);
            }
        }

        return objective;
    }
}