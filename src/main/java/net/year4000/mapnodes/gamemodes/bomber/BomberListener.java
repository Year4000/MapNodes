package net.year4000.mapnodes.gamemodes.bomber;

import net.year4000.mapnodes.game.NodeModeFactory;
import org.bukkit.event.Listener;

public class BomberListener implements Listener {
    private Bomber gameMode = (Bomber) NodeModeFactory.get().getGameMode(Bomber.class);
    private BomberConfig gameModeConfig = (BomberConfig) gameMode.getConfig();
}
