package net.year4000.mapnodes.game.regions.types;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.year4000.mapnodes.api.game.regions.PointVector;
import net.year4000.mapnodes.api.game.regions.Region;
import net.year4000.mapnodes.api.game.regions.RegionType;
import net.year4000.mapnodes.game.regions.RegionTypes;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;

@Data
@NoArgsConstructor
@RegionType(RegionTypes.GLOBAL)
public class Global implements Region {
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
}
