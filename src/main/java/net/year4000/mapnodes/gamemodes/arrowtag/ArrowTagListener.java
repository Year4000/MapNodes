package net.year4000.mapnodes.gamemodes.arrowtag;

import net.year4000.mapnodes.game.NodeModeFactory;
import org.bukkit.event.Listener;

public class ArrowTagListener implements Listener {
    private ArrowTag gameMode = (ArrowTag) NodeModeFactory.get().getGameMode(ArrowTag.class);
    private ArrowTagConfig gameModeConfig = (ArrowTagConfig) gameMode.getConfig();
}
