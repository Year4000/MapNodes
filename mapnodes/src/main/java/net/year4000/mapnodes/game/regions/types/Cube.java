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
import net.year4000.mapnodes.utils.Common;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

@RegionType(RegionTypes.CUBE)
public class Cube implements Region, Validator {
    private Point center = null;
    private Integer radius = null;
    private Integer height = null;
    private transient Integer yaw;
    private transient Integer pitch;

    // Cached vars
    private transient List<PointVector> cachedPoints = null;
    private transient List<Location> cachedLocations = null;

    public Cube() {
    }

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
            List<PointVector> locations = new ArrayList<>();
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
    public boolean inRegion(PointVector region) {
        Vector point = Common.pointToVector(region);

        return point.isInAABB(Common.pointToVector(getPoints().get(0)), Common.pointToVector(getPoints().get(getPoints().size() - 1)));
    }

    public Point getCenter() {
        return this.center;
    }

    public Integer getRadius() {
        return this.radius;
    }

    public Integer getHeight() {
        return this.height;
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

    public void setHeight(Integer height) {
        this.height = height;
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
        if (!(o instanceof Cube)) return false;
        final Cube other = (Cube) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$center = this.getCenter();
        final Object other$center = other.getCenter();
        if (this$center == null ? other$center != null : !this$center.equals(other$center)) return false;
        final Object this$radius = this.getRadius();
        final Object other$radius = other.getRadius();
        if (this$radius == null ? other$radius != null : !this$radius.equals(other$radius)) return false;
        final Object this$height = this.getHeight();
        final Object other$height = other.getHeight();
        if (this$height == null ? other$height != null : !this$height.equals(other$height)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $center = this.getCenter();
        result = result * PRIME + ($center == null ? 43 : $center.hashCode());
        final Object $radius = this.getRadius();
        result = result * PRIME + ($radius == null ? 43 : $radius.hashCode());
        final Object $height = this.getHeight();
        result = result * PRIME + ($height == null ? 43 : $height.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Cube;
    }

    public String toString() {
        return "net.year4000.mapnodes.game.regions.types.Cube(center=" + this.getCenter() + ", radius=" + this.getRadius() + ", height=" + this.getHeight() + ", yaw=" + this.getYaw() + ", pitch=" + this.getPitch() + ", cachedPoints=" + this.getCachedPoints() + ", cachedLocations=" + this.getCachedLocations() + ")";
    }
}
