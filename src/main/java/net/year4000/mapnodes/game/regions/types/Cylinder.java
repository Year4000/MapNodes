package net.year4000.mapnodes.game.regions.types;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.year4000.mapnodes.api.exceptions.InvalidJsonException;
import net.year4000.mapnodes.api.game.regions.PointVector;
import net.year4000.mapnodes.api.game.regions.Region;
import net.year4000.mapnodes.api.game.regions.RegionType;
import net.year4000.mapnodes.api.utils.Validator;
import net.year4000.mapnodes.game.regions.RegionTypes;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

@Data
@NoArgsConstructor
@RegionType(RegionTypes.CYLINDER)
public class Cylinder implements Region, Validator {
    private Point center = null;
    private Integer radius = null;
    private Integer height = null;
    private transient Integer yaw;
    private transient Integer pitch;

    // Cached vars
    private transient List<PointVector> cachedPoints = null;
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
    public List<PointVector> getPoints() {
        if (cachedPoints == null) {
            Set<PointVector> locations = new HashSet<>();
            int cx = center.getX();
            int cy = center.getY();
            int cz = center.getZ();

            // parametric equations for a cylinder:
            // x = radius * cos()
            // z = radius * sin()
            // y = height
            for (int y = cy; y <= cy + height; y++) {
                for (int i = 0 - radius; i <= radius; i++) {
                    for (int j = 0; j <= 360; j++) {
                        double x = i * Math.cos(Math.toRadians(j));
                        double z = i * Math.sin(Math.toRadians(j));

                        if (x > 0) {
                            x += 0.5;
                        }
                        else {
                            x -= 0.5;
                        }

                        if (z > 0) {
                            z += 0.5;
                        }
                        else {
                            z -= 0.5;
                        }

                        int xInt = cx + (int) x;
                        int zInt = cz + (int) z;
                        locations.add(new Point(xInt, y, zInt, yaw, pitch));
                    }
                }
            }

            cachedPoints = new ArrayList<PointVector>(locations);
        }

        return cachedPoints;
    }

    @Override
    public boolean inRegion(PointVector region) {
        return getPoints().contains(region);
    }
}
