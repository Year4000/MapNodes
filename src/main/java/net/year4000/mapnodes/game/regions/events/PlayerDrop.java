package net.year4000.mapnodes.game.regions.events;

import net.year4000.mapnodes.game.regions.EventType;
import net.year4000.mapnodes.game.regions.EventTypes;
import net.year4000.mapnodes.game.regions.RegionEvent;
import net.year4000.mapnodes.game.regions.RegionListener;
import org.bukkit.event.Listener;

@EventType(EventTypes.PLAYER_DROP)
public class PlayerDrop extends RegionEvent implements RegionListener {
}
