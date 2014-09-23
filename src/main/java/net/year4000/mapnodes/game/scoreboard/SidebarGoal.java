package net.year4000.mapnodes.game.scoreboard;

import lombok.Data;

@Data
public class SidebarGoal {
    /** The Goal Type for the score board */
    public enum GoalType {
        STATIC,
        DYNAMIC,
        ;
    }

    private GoalType type;
    private String display;
    private int score;
}
