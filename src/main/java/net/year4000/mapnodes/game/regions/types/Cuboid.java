package net.year4000.mapnodes.game.regions.types;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.year4000.mapnodes.exceptions.InvalidJsonException;
import net.year4000.mapnodes.game.regions.Region;
import net.year4000.mapnodes.game.regions.RegionType;
import net.year4000.mapnodes.game.regions.RegionTypes;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Validator;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

@Data
@NoArgsConstructor
@RegionType(RegionTypes.CUBOID)
public class Cuboid implements Region, Validator {
    private Point min = null;
    private Point max = null;
    private transient Integer yaw;
    private transient Integer pitch;

    // Cached vars
    private transient List<Point> cachedPoints = null;
    private transient List<Location> cachedLocations = null;

    @Override
    public void validate() throws InvalidJsonException {
        checkArgument(min != null, Msg.util("settings.region", "min"));

        min.validate();

        checkArgument(max != null, Msg.util("settings.region", "max"));

        max.validate();
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

            for (int y = min.getY(); y <= max.getY(); y++) {
                for (int x = min.getX(); x <= max.getX(); x++) {
                    for (int z = min.getZ(); z <= max.getZ(); z++) {
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
        boolean inX = min.getX() < region.getX() && max.getX() > region.getX();
        boolean inY = min.getY() < region.getY() && max.getY() > region.getY();
        boolean inZ = min.getZ() < region.getZ() && max.getZ() > region.getZ();

        return inX && inY && inZ;
    }
}
