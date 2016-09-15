/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.api.game.regions;

import org.bukkit.Location;
import org.bukkit.World;

public interface PointVector {
    public Integer getX();

    public Integer getY();

    public Integer getZ();

    public Location create(World world);
}
