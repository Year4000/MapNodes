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

@RegionType(RegionTypes.CHUNK)
public class Chunk implements Region, Validator {
    private static final int CHUNK_SIZE = 16;
    private static final int CHUNK_HEIGHT = 256;
    private Integer x = null;
    private Integer z = null;
    private transient Integer yaw;
    private transient Integer pitch;

    // Cached vars
    private transient List<PointVector> cachedPoints = null;
    private transient List<Location> cachedLocations = null;

    public Chunk() {
    }

    @Override
    public void validate() throws InvalidJsonException {
        checkArgument(x != null, Msg.util("settings.region", "x"));

        checkArgument(z != null, Msg.util("settings.region", "z"));
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

            int minX = this.x * CHUNK_SIZE;
            int minZ = this.z * CHUNK_SIZE;
            int maxX = (this.x * CHUNK_SIZE) + CHUNK_SIZE;
            int maxZ = (this.z * CHUNK_SIZE) + CHUNK_SIZE;

            for (int y = 0; y <= CHUNK_HEIGHT; y++) {
                for (int x = minX; x <= maxX; x++) {
                    for (int z = minZ; z <= maxZ; z++) {
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
        int minX = this.x * CHUNK_SIZE;
        int minZ = this.z * CHUNK_SIZE;
        int maxX = (this.x * CHUNK_SIZE) + CHUNK_SIZE;
        int maxZ = (this.z * CHUNK_SIZE) + CHUNK_SIZE;

        Vector point = Common.pointToVector(region);

        return point.isInAABB(new Vector(minX, 0, minZ), new Vector(maxX, CHUNK_HEIGHT, maxZ));
    }

    public Integer getX() {
        return this.x;
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

    public List<PointVector> getCachedPoints() {
        return this.cachedPoints;
    }

    public List<Location> getCachedLocations() {
        return this.cachedLocations;
    }

    public void setX(Integer x) {
        this.x = x;
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

    public void setCachedPoints(List<PointVector> cachedPoints) {
        this.cachedPoints = cachedPoints;
    }

    public void setCachedLocations(List<Location> cachedLocations) {
        this.cachedLocations = cachedLocations;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Chunk)) return false;
        final Chunk other = (Chunk) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$x = this.getX();
        final Object other$x = other.getX();
        if (this$x == null ? other$x != null : !this$x.equals(other$x)) return false;
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
        final Object $z = this.getZ();
        result = result * PRIME + ($z == null ? 43 : $z.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Chunk;
    }

    public String toString() {
        return "net.year4000.mapnodes.game.regions.types.Chunk(x=" + this.getX() + ", z=" + this.getZ() + ", yaw=" + this.getYaw() + ", pitch=" + this.getPitch() + ", cachedPoints=" + this.getCachedPoints() + ", cachedLocations=" + this.getCachedLocations() + ")";
    }
}
