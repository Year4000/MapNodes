/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.game.scoreboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.scoreboard.GameSidebarGoal;
import net.year4000.mapnodes.game.NodeGame;
import net.year4000.utilities.ChatColor;
import net.year4000.utilities.bukkit.MessageUtil;

@Data
@AllArgsConstructor
public class SidebarGoal implements GameSidebarGoal {
    private NodeGame game;
    private GoalType type;
    private String display;
    private Integer score;
    private String owner;

    public String getTeamDisplay(GamePlayer player) {
        if (player.getTeam().getId().equals(owner)) {
            return MessageUtil.replaceColors(display).replaceAll(ChatColor.COLOR_CHAR + "([0-9a-fA-F])", "&$1&o");
        }
        else {
            return display;
        }
    }

    @Override
    public void setType(GoalType type) {
        setSilentlyType(type);
        game.getScoreboardFactory().setAllGameSidebar();
    }

    @Override
    public void setDisplay(String display) {
        setSilentlyDisplay(display);
        game.getScoreboardFactory().setAllGameSidebar();
    }

    @Override
    public void setScore(Integer score) {
        setSilentlyScore(score);
        game.getScoreboardFactory().setAllGameSidebar();
    }

    @Override
    public void setOwner(String owner) {
        setSilentlyOwner(owner);
        game.getScoreboardFactory().setAllGameSidebar();
    }

    public void setSilentlyType(GoalType type) {
        this.type = type;
    }

    public void setSilentlyDisplay(String display) {
        this.display = display;
    }

    public void setSilentlyScore(Integer score) {
        this.score = score;
    }

    public void setSilentlyOwner(String owner) {
        this.owner = owner;
    }
}
