/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */

/** Represents a cuboid region with two positions */
class CuboidRegion extends AbstractRegion {

  /** point_one and point_two are both vector3 */
  constructor(point_one, point_two) {
    this._point_one = Conditions.not_null(point_one, 'point_one')
    this._point_two = Conditions.not_null(point_two, 'point_two')
  }

  /** Checks if the vector contains in this cuboid */
  contains(vector3) {
    let min = this._point_one.clone().min(this._point_two)
    let max = this._point_one.clone().max(this._point_two)
    let x = vector3.x >= min.x && vector3.x <= max.x
    let y = vector3.y >= min.y && vector3.y <= max.y
    let z = vector3.z >= min.z && vector3.z <= max.z
    return x && y && z
  }

  /** Generate all the points in this cuboid */
  get _points() {
    let points = new Immutable.Set.of();
    let min = this._point_one.clone().min(this._point_two)
    let max = this._point_one.clone().max(this._point_two)
    for (let y = min.y; y < max.y; y++) {
      for (let x = min.x; x < max.x; x++) {
        for (let z = min.z; z < max.z; z++) {
          points = points.add(new THREE.Vector3(x, y, z));
        }
      }
    }
    return points;
  }

  /** Get all the points this cuboid has */
  get points() {
    return _.memoize(this._points);
  }

  /** Checks if the two cuboids are equal */
  equals(other) {
    return other && _.isEqual(this._point_one, other._point_one) && _.isEqual(this._point_two, other._point_two);
  }
}
