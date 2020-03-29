/*
 * Copyright 2020 Year4000. All Rights Reserved.
 */
import _ from 'lodash'
import { Set } from 'immutable'


/** The abstract region that handles the creation of points */
export default class AbstractRegion {
  /** Get a immutable set of points the region contains, or if its not possible return empty */
  get points() {
    return Set.of()
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
