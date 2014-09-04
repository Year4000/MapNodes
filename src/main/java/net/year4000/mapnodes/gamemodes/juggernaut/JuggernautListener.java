package net.year4000.mapnodes.gamemodes.juggernaut;

import net.year4000.mapnodes.game.NodeModeFactory;
import org.bukkit.event.Listener;

public class JuggernautListener implements Listener {
    private Juggernaut gameMode = (Juggernaut) NodeModeFactory.get().getGameMode(Juggernaut.class);
    private JuggernautConfig gameModeConfig = (JuggernautConfig) gameMode.getConfig();
}
