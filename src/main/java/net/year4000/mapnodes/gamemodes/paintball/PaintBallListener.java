package net.year4000.mapnodes.gamemodes.paintball;

import net.year4000.mapnodes.game.NodeModeFactory;
import org.bukkit.event.Listener;

public class PaintBallListener implements Listener {
    private PaintBall gameMode = (PaintBall) NodeModeFactory.get().getGameMode(PaintBall.class);
    private PaintBallConfig gameModeConfig = (PaintBallConfig) gameMode.getConfig();

}
