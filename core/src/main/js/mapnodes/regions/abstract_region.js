/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
import { Set } from 'immutable'
import _ from 'lodash'

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
