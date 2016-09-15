/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.game.regions.types;

import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.regions.PointVector;
import net.year4000.mapnodes.api.game.regions.Region;
import net.year4000.mapnodes.api.game.regions.RegionType;
import net.year4000.mapnodes.api.game.regions.RegionTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.List;

@RegionType(RegionTypes.VOID)
public class Void implements Region {
    private static int WORLD_HEIGHT = Bukkit.getWorlds().get(0).getMaxHeight();

    public Void() {
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
        for (int i = 0; i < WORLD_HEIGHT; i++) {
            if (MapNodes.getCurrentWorld().getBlockAt(region.getX(), 0, region.getZ()).getType() != Material.AIR) {
                return false;
            }
        }

        return true;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Void)) return false;
        final Void other = (Void) o;
        if (!other.canEqual((Object) this)) return false;
        return true;
    }

    public int hashCode() {
        int result = 1;
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Void;
    }

    public String toString() {
        return "net.year4000.mapnodes.game.regions.types.Void()";
    }
}
