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
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BlockVector;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

@RegionType(RegionTypes.POINT)
public class Point implements Region, Validator, PointVector {
    private Integer x = null;
    private Integer y = null;
    private Integer z = null;
    private transient Integer yaw;
    private transient Integer pitch;

    public Point(Integer x, Integer y, Integer z) {
        this(x, y, z, null, null);
    }

    public Point(BlockVector vector) {
        this(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ(), null, null);
    }

    @java.beans.ConstructorProperties({"x", "y", "z", "yaw", "pitch"})
    public Point(Integer x, Integer y, Integer z, Integer yaw, Integer pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Point() {
    }

    @Override
    public void validate() throws InvalidJsonException {
        checkArgument(x != null, Msg.util("settings.region", "X"));

        checkArgument(y != null, Msg.util("settings.region", "Y"));

        checkArgument(z != null, Msg.util("settings.region", "Z"));
    }

    @Override
    public List<Location> getLocations(World world) {
        List<Location> locations = new ArrayList<>();

        locations.add(create(world));

        return locations;
    }

    @Override
    public List<PointVector> getPoints() {
        List<PointVector> locations = new ArrayList<>();

        locations.add(this);

        return locations;
    }

    @Override
    public boolean inRegion(PointVector region) {
        return region.getX().equals(x < 0 ? x - 1 : x) && region.getY().equals(y) && region.getZ().equals(z < 0 ? z - 1 : z);
    }

    public Location create(World world) {
        if (yaw == null || pitch == null) {
            return new Location(world, x < 0 ? x - 1 : x, y, z < 0 ? z - 1 : z);
        }

        return new Location(world, x < 0 ? x - 1 : x, y, z < 0 ? z - 1 : z, yaw, pitch);
    }

    public Integer getX() {
        return this.x;
    }

    public Integer getY() {
        return this.y;
    }

    public Integer getZ() {
        return this.z;
    }

    public Integer getYaw() {
        return this.yaw;
    }

    public Integer getPitch() {
        return this.pitch;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public void setZ(Integer z) {
        this.z = z;
    }

    public void setYaw(Integer yaw) {
        this.yaw = yaw;
    }

    public void setPitch(Integer pitch) {
        this.pitch = pitch;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Point)) return false;
        final Point other = (Point) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$x = this.getX();
        final Object other$x = other.getX();
        if (this$x == null ? other$x != null : !this$x.equals(other$x)) return false;
        final Object this$y = this.getY();
        final Object other$y = other.getY();
        if (this$y == null ? other$y != null : !this$y.equals(other$y)) return false;
        final Object this$z = this.getZ();
        final Object other$z = other.getZ();
        if (this$z == null ? other$z != null : !this$z.equals(other$z)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $x = this.getX();
        result = result * PRIME + ($x == null ? 43 : $x.hashCode());
        final Object $y = this.getY();
        result = result * PRIME + ($y == null ? 43 : $y.hashCode());
        final Object $z = this.getZ();
        result = result * PRIME + ($z == null ? 43 : $z.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Point;
    }

    public String toString() {
        return "net.year4000.mapnodes.game.regions.types.Point(x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + ", yaw=" + this.getYaw() + ", pitch=" + this.getPitch() + ")";
    }
}
