/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.game.scoreboard;

import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.scoreboard.GameSidebarGoal;
import net.year4000.mapnodes.game.NodeGame;
import net.year4000.utilities.ChatColor;
import net.year4000.utilities.bukkit.MessageUtil;

public class SidebarGoal implements GameSidebarGoal {
    private NodeGame game;
    private GoalType type;
    private String display;
    private Integer score;
    private String owner;

    @java.beans.ConstructorProperties({"game", "type", "display", "score", "owner"})
    public SidebarGoal(NodeGame game, GoalType type, String display, Integer score, String owner) {
        this.game = game;
        this.type = type;
        this.display = display;
        this.score = score;
        this.owner = owner;
    }

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

    public NodeGame getGame() {
        return this.game;
    }

    public GoalType getType() {
        return this.type;
    }

    public String getDisplay() {
        return this.display;
    }

    public Integer getScore() {
        return this.score;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setGame(NodeGame game) {
        this.game = game;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof SidebarGoal)) return false;
        final SidebarGoal other = (SidebarGoal) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$game = this.getGame();
        final Object other$game = other.getGame();
        if (this$game == null ? other$game != null : !this$game.equals(other$game)) return false;
        final Object this$type = this.getType();
        final Object other$type = other.getType();
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) return false;
        final Object this$display = this.getDisplay();
        final Object other$display = other.getDisplay();
        if (this$display == null ? other$display != null : !this$display.equals(other$display)) return false;
        final Object this$score = this.getScore();
        final Object other$score = other.getScore();
        if (this$score == null ? other$score != null : !this$score.equals(other$score)) return false;
        final Object this$owner = this.getOwner();
        final Object other$owner = other.getOwner();
        if (this$owner == null ? other$owner != null : !this$owner.equals(other$owner)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $game = this.getGame();
        result = result * PRIME + ($game == null ? 43 : $game.hashCode());
        final Object $type = this.getType();
        result = result * PRIME + ($type == null ? 43 : $type.hashCode());
        final Object $display = this.getDisplay();
        result = result * PRIME + ($display == null ? 43 : $display.hashCode());
        final Object $score = this.getScore();
        result = result * PRIME + ($score == null ? 43 : $score.hashCode());
        final Object $owner = this.getOwner();
        result = result * PRIME + ($owner == null ? 43 : $owner.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof SidebarGoal;
    }

    public String toString() {
        return "net.year4000.mapnodes.game.scoreboard.SidebarGoal(game=" + this.getGame() + ", type=" + this.getType() + ", display=" + this.getDisplay() + ", score=" + this.getScore() + ", owner=" + this.getOwner() + ")";
    }
}
