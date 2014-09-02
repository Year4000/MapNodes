package net.year4000.mapnodes.game;

import net.year4000.mapnodes.world.WorldManager;
import net.year4000.utilities.bukkit.MessageUtil;

public enum GameStage {
    WAITING,
    STARTING,
    PLAYING,
    ENDING,
    ENDED;

    /** Is the server running. */
    public static boolean isPlaying() {
        GameStage stage = WorldManager.get().getCurrentGame().getStage();
        return stage == GameStage.PLAYING;
    }

    /** Is the server running. */
    public static boolean isPreGame() {
        GameStage stage = WorldManager.get().getCurrentGame().getStage();
        return stage == GameStage.WAITING || stage == GameStage.STARTING;
    }

    /** Is the server running. */
    public static boolean isEndGame() {
        GameStage stage = WorldManager.get().getCurrentGame().getStage();
        return stage == GameStage.ENDED || stage == GameStage.ENDING;
    }
    /** Is the server running. */
    public static boolean isWaiting() {
        GameStage stage = WorldManager.get().getCurrentGame().getStage();
        return stage == GameStage.WAITING;
    }

    /** Is the server running. */
    public static boolean isStarting() {
        GameStage stage = WorldManager.get().getCurrentGame().getStage();
        return stage == GameStage.STARTING;
    }

    /** Is the server running. */
    public static boolean isEndding() {
        GameStage stage = WorldManager.get().getCurrentGame().getStage();
        return stage == GameStage.ENDING;
    }

    /** Is the server running. */
    public static boolean isEnded() {
        GameStage stage = WorldManager.get().getCurrentGame().getStage();
        return stage == GameStage.ENDED;
    }

    /** Get the color for the stage. */
    public static String getStageColor() {
        String color;

        if (isWaiting())
            color = "&e";
        else if (isStarting())
            color = "&2";
        else if (isPlaying())
            color = "&a";
        else if (isEndding())
            color = "&c";
        else
            color = "&4";

        return MessageUtil.replaceColors(color);
    }
}