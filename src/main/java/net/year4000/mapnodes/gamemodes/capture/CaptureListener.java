package net.year4000.mapnodes.gamemodes.capture;

import net.year4000.mapnodes.game.NodeModeFactory;
import org.bukkit.event.Listener;

public class CaptureListener implements Listener {
    private Capture gameMode = (Capture) NodeModeFactory.get().getGameMode(Capture.class);
    private CaptureConfig gameModeConfig = (CaptureConfig) gameMode.getConfig();

}
