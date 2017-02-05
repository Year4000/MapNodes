/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.game.regions;

import com.flowpowered.math.vector.Vector3i;
import net.year4000.utilities.Utils;

import java.util.Optional;
import java.util.Set;

/** A region that contains everything */
public final class GlobalRegion extends AbstractRegion {
    @Override
    public Optional<Set<Vector3i>> getPoints() {
        return Optional.empty();
    }

    @Override
    public boolean contains(Vector3i vector3i) {
        return true;
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
