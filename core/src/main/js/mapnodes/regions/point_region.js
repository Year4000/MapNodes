/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
import { Vector3 } from 'three'
import { Set } from 'immutable'
import AbstractRegion from './abstract_region.js'

/** A region that contains a single point, must wrap with THREE.Vector3 */
export default class PointRegion extends AbstractRegion {

  constructor(x, y, z) {
    super()
    this._point = new Vector3(x, y, z)
  }

  /** Get the point of this region */
  get points() {
    return Set.of(this._point)
  }

  /** Get the x cord */
  get x() {
    return this._point.x
  }

  /** Get the y cord */
  get y() {
    return this._point.y
  }

  /** Get the z cord */
  get z() {
     return this._point.z
   }

  /** Create a copy of the point for vector3 */
  clone() {
    return this._point.clone()
  }

  // Override
  valueOf() {
    // todo support for Immutable Sets
    return (this.x * 211 + this.y) * 97 + this.z
  }
}
