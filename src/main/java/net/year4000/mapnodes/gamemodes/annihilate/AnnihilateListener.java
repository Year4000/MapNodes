package net.year4000.mapnodes.gamemodes.annihilate;

import net.year4000.mapnodes.game.NodeModeFactory;
import org.bukkit.event.Listener;

public class AnnihilateListener implements Listener {
    private Annihilate gameMode = (Annihilate) NodeModeFactory.get().getGameMode(Annihilate.class);
    private AnnihilateConfig gameModeConfig = (AnnihilateConfig) gameMode.getConfig();
}
