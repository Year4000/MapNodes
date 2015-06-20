/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.api.game.scoreboard;

import net.year4000.mapnodes.api.game.GamePlayer;

public interface GameSidebarGoal {
    public GoalType getType();

    public void setType(GoalType type);

    public String getDisplay();

    public void setDisplay(String display);

    public Integer getScore();

    public void setScore(Integer score);

    public String getOwner();

    public void setOwner(String owner);

    public String getTeamDisplay(GamePlayer player);

    /** The Goal Type for the score board */
    public enum GoalType {
        STATIC,
        DYNAMIC,;
    }
}
