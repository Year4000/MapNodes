/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
import { Vector3 } from 'three';
import _ from 'lodash'
import AbstractRegion from './abstract_region.js'
import CuboidRegion from './cuboid_region.js'
import Conditions from '../conditions.js'

/** Represents a cube region that wraps a cuboid region */
export default class CubeRegion extends AbstractRegion {

  /** Center is a vector3, when width is not there height becomes radius */
  constructor(center, height, width) {
    super()
    Conditions.not_null(center, 'center')
    Conditions.not_null(height, 'height')
    width = width || height
    let point_one = new Vector3(center.x - width, center.y - height, center.z - width)
    let point_two = new Vector3(center.x + width, center.y + height, center.z + width)
    this._cuboid = new CuboidRegion(point_one, point_two)
  }

  /** Get all the immutable set of points for this cube */
  get points() {
    return this._cuboid.points
  }

  /** Checks if the vector is within this cube */
  contains(vector3) {
    return this._cuboid.contains(vector3)
  }

  /** Checks if the cubes are equal */
  equals(other) {
    return other && _.isEqual(this._cuboid, other._cuboid)
  }
}
