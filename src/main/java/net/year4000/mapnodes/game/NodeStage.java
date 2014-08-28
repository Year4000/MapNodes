package net.year4000.mapnodes.game;

import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.GameStage;
import net.year4000.utilities.bukkit.MessageUtil;

public enum NodeStage implements GameStage {
    WAITING,
    STARTING,
    PLAYING,
    ENDING,
    ENDED;

    /** Is the server running. */
    public boolean isPlaying() {
        GameStage stage = MapNodes.getCurrentGame().getStage();
        return stage == NodeStage.PLAYING;
    }

    /** Is the server running. */
    public boolean isPreGame() {
        GameStage stage = MapNodes.getCurrentGame().getStage();
        return stage == NodeStage.WAITING || stage == NodeStage.STARTING;
    }

    /** Is the server running. */
    public boolean isEndGame() {
        GameStage stage = MapNodes.getCurrentGame().getStage();
        return stage == NodeStage.ENDED || stage == NodeStage.ENDING;
    }
    /** Is the server running. */
    public boolean isWaiting() {
        GameStage stage = MapNodes.getCurrentGame().getStage();
        return stage == NodeStage.WAITING;
    }

    /** Is the server running. */
    public boolean isStarting() {
        GameStage stage = MapNodes.getCurrentGame().getStage();
        return stage == NodeStage.STARTING;
    }

    /** Is the server running. */
    public boolean isEnding() {
        GameStage stage = MapNodes.getCurrentGame().getStage();
        return stage == NodeStage.ENDING;
    }

    /** Is the server running. */
    public boolean isEnded() {
        GameStage stage = MapNodes.getCurrentGame().getStage();
        return stage == NodeStage.ENDED;
    }

    /** Get the color for the stage. */
    public String getStageColor() {
        String color;

        if (isWaiting())
            color = "&e";
        else if (isStarting())
            color = "&2";
        else if (isPlaying())
            color = "&a";
        else if (isEnding())
            color = "&c";
        else
            color = "&4";

        return MessageUtil.message(color);
    }
}

