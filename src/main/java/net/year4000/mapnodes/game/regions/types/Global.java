package net.year4000.mapnodes.game.regions.types;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.year4000.mapnodes.game.regions.Region;
import net.year4000.mapnodes.game.regions.RegionType;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;

@Data
@NoArgsConstructor
@RegionType("global")
public class Global implements Region {
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
        return true;
    }
}
