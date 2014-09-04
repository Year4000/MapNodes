package net.year4000.mapnodes.gamemodes.magewars;

import net.year4000.mapnodes.game.NodeModeFactory;
import org.bukkit.event.Listener;

public class MageWarsListener implements Listener {
    private MageWars gameMode = (MageWars) NodeModeFactory.get().getGameMode(MageWars.class);
    private MageWarsConfig gameModeConfig = (MageWarsConfig) gameMode.getConfig();
}
