package net.year4000.mapnodes.gamemodes.deathmatch;

import net.year4000.mapnodes.game.NodeModeFactory;
import org.bukkit.event.Listener;

public class DeathmatchListener implements Listener {
    private Deathmatch gameMode = (Deathmatch) NodeModeFactory.get().getGameMode(Deathmatch.class);
    private DeathmatchConfig gameModeConfig = (DeathmatchConfig) gameMode.getConfig();
}
