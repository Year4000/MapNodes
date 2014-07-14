package net.year4000.mapnodes.api.game;

public interface GameStage {
    public boolean isPlaying();

    public boolean isPreGame();

    public boolean isEndGame();

    public boolean isWaiting();

    public boolean isStarting();

    public boolean isEnding();

    public boolean isEnded();

    public String getStageColor();
}
