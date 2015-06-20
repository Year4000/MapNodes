/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

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
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private transient List<PointVector> cachedPoints = null;
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
    public List<PointVector> getPoints() {
        if (cachedPoints == null) {
            int cx = center.getX();
            int cy = center.getY();
            int cz = center.getZ();
            Set<Point> locations = new HashSet<>();

            // parametric equations for sphere are:
            // z = r * cos();
            // x = sqrt(r^2 - z^2) * cos();
            // y = sqrt(r^2 - z^2) * sin();
            for (int i = 0 - radius; i <= radius; i++) {
                for (int k = 0; k <= 180; k++) {
                    int z = (int) (i * Math.cos(Math.toRadians(k)));

                    for (int j = 0; j <= 360; j++) {
                        double x = (Math.sqrt(Math.pow(i, 2) - Math.pow(z, 2)) * Math.cos(Math.toRadians(j)));
                        double y = (Math.sqrt(Math.pow(i, 2) - Math.pow(z, 2)) * Math.sin(Math.toRadians(j)));

                        if (x > 0) {
                            x += 0.5;
                        }
                        else {
                            x -= 0.5;
                        }

                        if (y > 0) {
                            y += 0.5;
                        }
                        else {
                            y -= 0.5;
                        }

                        int xInt = cx + (int) x;
                        int yInt = cy + (int) y;
                        int zInt = cz + z;
                        locations.add(new Point(xInt, yInt, zInt, yaw, pitch));

                        // todo test as this may not be necessary, also may not be enough.
                        xInt = cx + (int) (Math.sqrt(Math.pow(i, 2) - Math.pow(z, 2)) * Math.cos(Math.toRadians(j)));
                        yInt = cy + (int) (Math.sqrt(Math.pow(i, 2) - Math.pow(z, 2)) * Math.sin(Math.toRadians(j)));
                        locations.add(new Point(xInt, yInt, zInt, yaw, pitch));
                    }
                }
            }

            cachedPoints = new ArrayList<>(locations);
        }

        return cachedPoints;
    }

    @Override
    public boolean inRegion(PointVector region) {
        Vector point = new Vector(region.getX(), region.getY(), region.getZ());
        return point.isInSphere(new Vector(center.getX(), center.getY(), center.getZ()), radius);
    }
}
