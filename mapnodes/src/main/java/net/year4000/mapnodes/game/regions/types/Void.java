/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.game.regions.types;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.regions.PointVector;
import net.year4000.mapnodes.api.game.regions.Region;
import net.year4000.mapnodes.api.game.regions.RegionType;
import net.year4000.mapnodes.api.game.regions.RegionTypes;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.List;

@Data
@NoArgsConstructor
@RegionType(RegionTypes.VOID)
public class Void implements Region {
    private static int WORLD_HEIGHT = 256;

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
}
