package net.year4000.mapnodes.gamemodes.endtimes;

import net.year4000.mapnodes.game.NodeModeFactory;
import org.bukkit.event.Listener;

public class EndTimesListener implements Listener {
    private EndTimes gameMode = (EndTimes) NodeModeFactory.get().getGameMode(EndTimes.class);
    private EndTimesConfig gameModeConfig = (EndTimesConfig) gameMode.getConfig();
}
