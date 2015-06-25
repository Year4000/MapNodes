/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.api.game.regions;

import net.year4000.mapnodes.api.game.GameRegion;
import org.bukkit.event.Listener;

public interface RegionListener extends Listener {
    /** Assign the event the region it belongs to */
    public void setRegion(GameRegion region);
}
