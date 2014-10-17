package net.year4000.mapnodes.game.regions.types;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.year4000.mapnodes.exceptions.InvalidJsonException;
import net.year4000.mapnodes.game.regions.Region;
import net.year4000.mapnodes.game.regions.RegionType;
import net.year4000.mapnodes.game.regions.RegionTypes;
import net.year4000.mapnodes.utils.Validator;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

@Data
@NoArgsConstructor
@RegionType(RegionTypes.CUBE)
public class Cube implements Region, Validator {
    private Point center = null;
    private Integer radius = null;
    private Integer height = null;
    private transient Integer yaw;
    private transient Integer pitch;

    // Cached vars
    private transient List<Point> cachedPoints = null;
    private transient List<Location> cachedLocations = null;

    @Override
    public void validate() throws InvalidJsonException {
        checkArgument(center != null);
        center.validate();
        checkArgument(radius != null && radius != 0);
        checkArgument(height != null && height != 0);
    }

    @Override
    public List<Location> getLocations(World world) {
        if (cachedLocations == null) {
            cachedLocations = getPoints().stream().map(p -> p.create(world)).collect(Collectors.toList());
        }

        return cachedLocations;
    }

    @Override
    public List<Point> getPoints() {
        if (cachedPoints == null) {
            List<Point> locations = new ArrayList<>();
            int cx = center.getX();
            int cy = center.getY();
            int cz = center.getZ();

            for (int x = cx - radius; x <= cx + radius; x++) {
                for (int z = cz - radius; z <= cz + radius; z++) {
                    for (int y = cy; y < cy + height; y++) {
                        locations.add(new Point(x, y, z, yaw, pitch));
                    }
                }
            }

            cachedPoints = locations;
        }

        return cachedPoints;
    }

    @Override
    public boolean inRegion(Point region) {
        boolean inX = center.getX() - radius < region.getX() && center.getX() + radius > region.getX();
        boolean inY = center.getY() < region.getY() && center.getY() + height > region.getY();
        boolean inZ = center.getZ() - radius < region.getZ() && center.getZ() + radius > region.getZ();

        return inX && inY && inZ;
    }
}
