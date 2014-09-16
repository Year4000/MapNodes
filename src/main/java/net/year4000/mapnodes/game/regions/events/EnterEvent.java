package net.year4000.mapnodes.game.regions.events;

import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.game.regions.EventType;
import net.year4000.mapnodes.game.regions.EventTypes;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

@EventType(EventTypes.ENTER)
public class EnterEvent extends BaseRegionEvent implements Listener {
}
