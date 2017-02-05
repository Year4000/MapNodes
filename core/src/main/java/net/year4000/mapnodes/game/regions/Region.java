/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.game.regions;

import com.flowpowered.math.vector.Vector3i;

import java.util.Optional;
import java.util.Set;

/** Region interface that is the base of the region */
public interface Region {
    /** Get a immutable set of points the region contains, or if its not possible return empty */
    Optional<Set<Vector3i>> getPoints();

    /** Check when the vector is in the region */
    boolean contains(Vector3i vector3i);
}
