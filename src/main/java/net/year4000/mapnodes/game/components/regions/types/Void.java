package net.year4000.mapnodes.game.components.regions.types;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.game.components.regions.Region;
import net.year4000.mapnodes.game.components.regions.RegionType;
import net.year4000.mapnodes.utils.Validator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.List;

@Data
@NoArgsConstructor
@RegionType("void")
public class Void implements Region {
    private static int WORLD_HEIGHT = 256;
    @Override
    public List<Location> getLocations(World world) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Point> getPoints() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean inRegion(Point region) {
        for (int i = 0; i < WORLD_HEIGHT; i++) {
            if (MapNodes.getCurrentWorld().getBlockAt(region.getX(), 0, region.getZ()).getType() != Material.AIR) {
                return false;
            }
        }

        return true;
    }
}
