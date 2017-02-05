/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.regions;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableSet;
import net.year4000.utilities.Conditions;
import net.year4000.utilities.Utils;

/** Represents a cuboid region with two positions */
public final class CuboidRegion extends AbstractComplexRegion {
    private Vector3i pointOne;
    private Vector3i pointTwo;

    public CuboidRegion(Vector3i pointOne, Vector3i pointTwo) {
        this.pointOne = Conditions.nonNull(pointOne, "pointOne");
        this.pointTwo = Conditions.nonNull(pointTwo, "pointTwo");
    }

    /** Generate the points of the cuboid region */
    @Override
    protected ImmutableSet<Vector3i> generatePoints() {
        ImmutableSet.Builder<Vector3i> points = ImmutableSet.builder();
        Vector3i min = pointOne.min(pointTwo);
        Vector3i max = pointOne.max(pointTwo);
        for (int y = min.getY(); y < max.getY(); y++) {
            for (int x = min.getX(); x < max.getX(); x++) {
                for (int z = min.getZ(); z < max.getZ(); z++) {
                    points.add(new Vector3i(x, y, z));
                }
            }
        }
        return points.build();
    }

    @Override
    public boolean contains(Vector3i vector3i) {
        Vector3i min = pointOne.min(pointTwo);
        Vector3i max = pointOne.max(pointTwo);
        boolean x = vector3i.getX() >= min.getX() && vector3i.getX() <= max.getX();
        boolean y = vector3i.getY() >= min.getY() && vector3i.getY() <= max.getY();
        boolean z = vector3i.getZ() >= min.getZ() && vector3i.getZ() <= max.getZ();
        return x && y && z;
    }

    @Override
    public boolean equals(Object other) {
        return Utils.equals(this, other);
    }

    @Override
    public int hashCode() {
        return Utils.hashCode(this);
    }

    @Override
    public String toString() {
        return Utils.toString(this);
    }
}
