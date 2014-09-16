package net.year4000.mapnodes.game.regions.events;

import net.year4000.mapnodes.game.regions.EventType;
import net.year4000.mapnodes.game.regions.EventTypes;
import net.year4000.mapnodes.game.regions.RegionEvent;
import org.bukkit.event.Listener;

@EventType(EventTypes.KILL_PLAYER)
public class KillPlayer extends RegionEvent implements Listener {
}
