package net.year4000.mapnodes.gamemodes.skywars;

import net.year4000.mapnodes.game.NodeModeFactory;
import org.bukkit.event.Listener;

public class SkywarsListener implements Listener {
    private Skywars gameMode = (Skywars) NodeModeFactory.get().getGameMode(Skywars.class);
    private SkywarsListener gameModeConfig = (SkywarsListener) gameMode.getConfig();

}
