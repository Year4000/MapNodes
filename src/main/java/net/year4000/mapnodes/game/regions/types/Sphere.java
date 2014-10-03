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
@RegionType(RegionTypes.SPHERE)
public class Sphere implements Region, Validator {
    private Point center = null;
    private Integer radius = null;
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

            // parametric equations for sphere are:
            // z = r * cos();
            // x = sqrt(r^2 - z^2) * cos();
            // y = sqrt(r^2 - z^2) * sin();
            for (int i = 0 - radius; i <= radius; i++) {
                for (int j = 0; j <= 360; j++) {
                    int z = (int) (radius * Math.cos(Math.toRadians(j)));
                    int x = (int) (Math.sqrt(Math.pow(radius, 2) - Math.pow(z, 2)) * Math.cos(Math.toRadians(j)));
                    int y = (int) (Math.sqrt(Math.pow(radius, 2) - Math.pow(z, 2)) * Math.sin(Math.toRadians(j)));
                    // System.out.println((int) Math.cos(Math.toRadians(j)));
                    locations.add(new Point(cx + x, cy + y, cz + z, yaw, pitch));
                }
            }

            cachedPoints = locations;
        }

        return cachedPoints;
    }

    @Override
    public boolean inRegion(Point region) {
        return getPoints().contains(region);
    }
}
