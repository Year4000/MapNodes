/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.game.regions;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableSet;
import net.year4000.utilities.Conditions;

import java.lang.ref.SoftReference;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractComplexRegion extends AbstractRegion {
    /** The soft reference of the points the region contains */
    protected transient SoftReference<Set<Vector3i>> points;

    /** Generate the points for this region */
    protected abstract ImmutableSet<Vector3i> generatePoints();

    /** Get the set of points generated */
    protected Set<Vector3i> points() {
        if (points == null || points.get() == null) {
            points = new SoftReference<>(Conditions.nonNull(generatePoints(), "generatePoints()"));
        }
        return points.get();
    }

    @Override
    public Optional<Set<Vector3i>> getPoints() {
        return Optional.of(points());
    }
}
