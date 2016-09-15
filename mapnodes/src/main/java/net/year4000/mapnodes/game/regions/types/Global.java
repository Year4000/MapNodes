/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.game.regions.types;

import net.year4000.mapnodes.api.game.regions.PointVector;
import net.year4000.mapnodes.api.game.regions.Region;
import net.year4000.mapnodes.api.game.regions.RegionType;
import net.year4000.mapnodes.api.game.regions.RegionTypes;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;

@RegionType(RegionTypes.GLOBAL)
public class Global implements Region {
    public Global() {
    }

    @Override
    public List<Location> getLocations(World world) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<PointVector> getPoints() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean inRegion(PointVector region) {
        return true;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Global)) return false;
        final Global other = (Global) o;
        if (!other.canEqual((Object) this)) return false;
        return true;
    }

    public int hashCode() {
        int result = 1;
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Global;
    }

    public String toString() {
        return "net.year4000.mapnodes.game.regions.types.Global()";
    }
}
