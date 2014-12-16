package net.year4000.mapnodes.gamemodes.skywars;

import net.year4000.mapnodes.api.events.game.GameLoadEvent;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.gamemodes.elimination.Elimination;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

@GameModeInfo(
    name = "Skywars",
    version = "1.0",
    config = SkywarsConfig.class
)
public class Skywars extends Elimination {

    @EventHandler(priority = EventPriority.HIGH)
    public void onLoadSkyWars(GameLoadEvent event) {
        // todo make classes global for all maps
    }

    // todo After x time go to sudden death and start blowing up islands
}
