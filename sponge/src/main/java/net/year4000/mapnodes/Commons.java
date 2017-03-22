/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes;

import com.flowpowered.math.vector.Vector3d;
import net.year4000.utilities.utils.UtilityConstructError;

/** Common methods that should really be in Utilities */
public final class Commons {
  private Commons() {
    UtilityConstructError.raise();
  }

  /** Center the cords */
  public static Vector3d center(double x, double y, double z) {
    return new Vector3d((int) x, (int) y, (int) z).add(0.5, 0.5, 0.5);
  }
}
