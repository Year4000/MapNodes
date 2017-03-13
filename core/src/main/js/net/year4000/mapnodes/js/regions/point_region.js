/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */

/** A region that contains a single point */
class PointRegion extends AbstractRegion {

  constructor(x, y, z) {
    this._point = new THREE.Vector3(x, y, z)
  }

  /** Get the point of this region */
  get points() {
    return new Immutable.Set.of(this._point)
  }
}
