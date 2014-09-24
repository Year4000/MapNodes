package net.year4000.mapnodes.game.scoreboard;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SidebarGoal {
    /** The Goal Type for the score board */
    public enum GoalType {
        STATIC,
        DYNAMIC,
        ;
    }

    private GoalType type;
    private String display;
    private Integer score;
}
