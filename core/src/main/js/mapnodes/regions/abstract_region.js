/*
 * Copyright 2020 Year4000. All Rights Reserved.
 */
import _ from 'lodash'
import { Set } from 'immutable'


/** @typedef {import('three').Vector3} Vector3 */

/** The abstract region that handles the creation of points */
export default class AbstractRegion {
  /**
   * Get a immutable set of points the region contains, or if its not possible return empty
   *
   * @return {Set<Vector3>}
   */
  get points() {
    return Set.of()
  }

  /**
   * Check when the vector is in the region
   *
   * @param {Vector3} vector3
   * @return {boolean}
   */
  contains(vector3) {
    return this.points.has(vector3)
  }

  /**
   * Are both regions equal
   *
   * @param {AbstractRegion} other
   * @return {boolean}
   */
  equals(other) {
    return _.isEqual(this, other)
  }
}
