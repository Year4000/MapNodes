/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.game.regions;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableSet;
import net.year4000.utilities.Conditions;
import net.year4000.utilities.Utils;

/** A region that contains a sphere */
public final class SphereRegion extends AbstractComplexRegion {
    private Vector3i center;
    private int radius;

    public SphereRegion(Vector3i center, int radius) {
        this.center = Conditions.nonNull(center, "center");
        this.radius = Conditions.isLarger(radius, 0);
    }

    /** Generate the points of the sphere region */
    @Override
    protected ImmutableSet<Vector3i> generatePoints() {
        ImmutableSet.Builder<Vector3i> points = ImmutableSet.builder();
        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    Vector3i point = new Vector3i(center.getX() + x, center.getY() + y, center.getZ() + z);
                    if (contains(point)) {
                        points.add(point);
                    }
                }
            }
        }
        return points.build();
    }

    @Override
    public boolean contains(Vector3i vector3i) {
        return center.distance(vector3i) <= radius;
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
