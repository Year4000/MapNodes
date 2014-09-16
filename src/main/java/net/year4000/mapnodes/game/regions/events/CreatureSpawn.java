package net.year4000.mapnodes.game.regions.events;

import net.year4000.mapnodes.game.regions.EventType;
import net.year4000.mapnodes.game.regions.EventTypes;
import net.year4000.mapnodes.game.regions.RegionEvent;
import org.bukkit.event.Listener;

@EventType(EventTypes.CREATURE_SPAWN)
public class CreatureSpawn extends RegionEvent implements Listener {
}
