/*
 * Copyright 2020 Year4000. All Rights Reserved.
 */
import _ from 'lodash'
import { Set } from 'immutable'
import { Vector3 } from 'three'

import AbstractRegion from './abstract_region.js'
import { not_null } from '../conditions.js'


/** Represents a cuboid region with two positions */
export default class CuboidRegion extends AbstractRegion {
  /**
   * point_one and point_two are both vector3
   *
   * @param {Vector3} point_one
   * @param {Vector3} point_two
   */
  constructor(point_one, point_two) {
    super()
    this._point_one = not_null(point_one, 'point_one')
    this._point_two = not_null(point_two, 'point_two')
    // Set point_one as min and point_two as max
    this._point_one = this._point_one.clone().min(this._point_two)
    this._point_two = this._point_one.clone().max(this._point_two)
  }

  /**
   * Checks if the vector contains in this cuboid
   *
   * @param {Vector3} vector3
   * @return {boolean}
   */
  contains(vector3) {
    const min = this._point_one
    const max = this._point_two
    const x = vector3.x >= min.x && vector3.x <= max.x
    const y = vector3.y >= min.y && vector3.y <= max.y
    const z = vector3.z >= min.z && vector3.z <= max.z
    return x && y && z
  }

  /**
   * Generate all the points in this cuboid
   *
   * @return {Set<Vector3>}
   */
  _points() {
    let points = Set.of()
    const min = this._point_one
    const max = this._point_two
    for (let { y } = min; y < max.y; y++) {
      for (let { x } = min; x < max.x; x++) {
        for (let { z } = min; z < max.z; z++) {
          points = points.add(new Vector3(x, y, z))
        }
      }
    }
    return points
  }

  /** Get all the points this cuboid has */
  get points() {
    // todo cache the results in some type of weak var
    if (!this.__points) {
      this.__points = this._points()
    }
    return this.__points
  }

  /**
   * Checks if the two cuboids are equal
   *
   * @param {CuboidRegion} other
   * @return {boolean}
   */
  equals(other) {
    return other && _.isEqual(this._point_one, other._point_one) && _.isEqual(this._point_two, other._point_two)
  }
}
