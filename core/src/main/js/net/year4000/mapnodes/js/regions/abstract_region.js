/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */

/** The abstract region that handles the creation of points */
class AbstractRegion {

  /** Get a immutable set of points the region contains, or if its not possible return empty */
  get points() {
    return Immutable.Set.of()
  }

  /** Check when the vector is in the region */
  contains(vector3) {
    return this.points.has(vector3)
  }

  /** Are both regions equal */
  equals(other) {
    return _.isEqual(this, other)
  }
}
