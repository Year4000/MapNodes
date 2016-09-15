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
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Common;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

@RegionType(RegionTypes.CUBOID)
public class Cuboid implements Region, Validator {
    private Point min = null;
    private Point max = null;
    private transient Integer yaw;
    private transient Integer pitch;

    // Cached vars
    private transient List<PointVector> cachedPoints = null;
    private transient List<Location> cachedLocations = null;

    public Cuboid() {
    }

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
    public List<PointVector> getPoints() {
        if (cachedPoints == null) {
            List<PointVector> locations = new ArrayList<>();

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
    public boolean inRegion(PointVector region) {
        Vector point = Common.pointToVector(region);

        return point.isInAABB(Common.pointToVector(min), Common.pointToVector(max));
    }

    public Point getMin() {
        return this.min;
    }

    public Point getMax() {
        return this.max;
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

    public void setMin(Point min) {
        this.min = min;
    }

    public void setMax(Point max) {
        this.max = max;
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
        if (!(o instanceof Cuboid)) return false;
        final Cuboid other = (Cuboid) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$min = this.getMin();
        final Object other$min = other.getMin();
        if (this$min == null ? other$min != null : !this$min.equals(other$min)) return false;
        final Object this$max = this.getMax();
        final Object other$max = other.getMax();
        if (this$max == null ? other$max != null : !this$max.equals(other$max)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $min = this.getMin();
        result = result * PRIME + ($min == null ? 43 : $min.hashCode());
        final Object $max = this.getMax();
        result = result * PRIME + ($max == null ? 43 : $max.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Cuboid;
    }

    public String toString() {
        return "net.year4000.mapnodes.game.regions.types.Cuboid(min=" + this.getMin() + ", max=" + this.getMax() + ", yaw=" + this.getYaw() + ", pitch=" + this.getPitch() + ", cachedPoints=" + this.getCachedPoints() + ", cachedLocations=" + this.getCachedLocations() + ")";
    }
}
