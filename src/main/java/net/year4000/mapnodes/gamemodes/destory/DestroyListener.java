package net.year4000.mapnodes.gamemodes.destory;

import net.year4000.mapnodes.game.NodeModeFactory;
import org.bukkit.event.Listener;

public class DestroyListener implements Listener {
    private Destroy gameMode = (Destroy) NodeModeFactory.get().getGameMode(Destroy.class);
    private DestroyConfig gameModeConfig = (DestroyConfig) gameMode.getConfig();
}
