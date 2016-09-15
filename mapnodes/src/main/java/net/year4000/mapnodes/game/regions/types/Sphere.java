/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.game.regions.types;

import net.year4000.mapnodes.api.exceptions.InvalidJsonException;
import net.year4000.mapnodes.api.game.regions.PointVector;
import net.year4000.mapnodes.api.game.regions.Region;
import net.year4000.mapnodes.api.game.regions.RegionType;
import net.year4000.mapnodes.api.game.regions.RegionTypes;
import net.year4000.mapnodes.api.utils.Validator;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

@RegionType(RegionTypes.SPHERE)
public class Sphere implements Region, Validator {
    private Point center = null;
    private Integer radius = null;
    private transient Integer yaw;
    private transient Integer pitch;

    // Cached vars
    private transient List<PointVector> cachedPoints = null;
    private transient List<Location> cachedLocations = null;

    public Sphere() {
    }

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

    public Point getCenter() {
        return this.center;
    }

    public Integer getRadius() {
        return this.radius;
    }

    public Integer getYaw() {
        return this.yaw;
    }

    public Integer getPitch() {
        return this.pitch;
    }

    public List<PointVector> getCachedPoints() {
        return this.cachedPoints;
    }

    public List<Location> getCachedLocations() {
        return this.cachedLocations;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public void setRadius(Integer radius) {
        this.radius = radius;
    }

    public void setYaw(Integer yaw) {
        this.yaw = yaw;
    }

    public void setPitch(Integer pitch) {
        this.pitch = pitch;
    }

    public void setCachedPoints(List<PointVector> cachedPoints) {
        this.cachedPoints = cachedPoints;
    }

    public void setCachedLocations(List<Location> cachedLocations) {
        this.cachedLocations = cachedLocations;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Sphere)) return false;
        final Sphere other = (Sphere) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$center = this.getCenter();
        final Object other$center = other.getCenter();
        if (this$center == null ? other$center != null : !this$center.equals(other$center)) return false;
        final Object this$radius = this.getRadius();
        final Object other$radius = other.getRadius();
        if (this$radius == null ? other$radius != null : !this$radius.equals(other$radius)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $center = this.getCenter();
        result = result * PRIME + ($center == null ? 43 : $center.hashCode());
        final Object $radius = this.getRadius();
        result = result * PRIME + ($radius == null ? 43 : $radius.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Sphere;
    }

    public String toString() {
        return "net.year4000.mapnodes.game.regions.types.Sphere(center=" + this.getCenter() + ", radius=" + this.getRadius() + ", yaw=" + this.getYaw() + ", pitch=" + this.getPitch() + ", cachedPoints=" + this.getCachedPoints() + ", cachedLocations=" + this.getCachedLocations() + ")";
    }
}
