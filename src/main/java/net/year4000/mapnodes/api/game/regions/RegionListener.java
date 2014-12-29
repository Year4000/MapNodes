package net.year4000.mapnodes.api.game.regions;

import net.year4000.mapnodes.game.NodeRegion;
import org.bukkit.event.Listener;

public interface RegionListener extends Listener {
    /** Assign the event the region it belongs to */
    public void setRegion(NodeRegion region);
}
