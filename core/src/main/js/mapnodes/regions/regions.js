/*
 * Copyright 2020 Year4000. All Rights Reserved.
 */
import _ from 'lodash'

import AbstractRegion from './abstract_region.js'
import CubeRegion from './cube_region.js'
import CuboidRegion from './cuboid_region.js'
import GlobalRegion from './global_region.js'
import PointRegion from './point_region.js'


/** The known regions the system knows how to handle */
const Regions = {
  /** The map from key to instance type */
  REGION_MAP: {
    point: PointRegion,
    cube: CubeRegion,
    cuboid: CuboidRegion,
    global: GlobalRegion,
  },

  /** Functions to reduce the JSON object into its proper region class */
  REGION_CONSTRUCT: {
    global: () => new GlobalRegion(),
    point: (obj) => {
      const cords = _.map(_.split(_.replace(obj.xyz, / /, ''), ',', 3), (cord) => _.toNumber(cord))
      return new PointRegion(...cords)
    },
    cube: (obj) => {
      const center = Regions.REGION_CONSTRUCT.point(obj.center)
      return new CubeRegion(center, obj.radius, obj.height)
    },
    cuboid: (obj) => {
      const min = Regions.REGION_CONSTRUCT.point(obj.min)
      const max = Regions.REGION_CONSTRUCT.point(obj.max)
      return new CuboidRegion(min, max)
    },
    chunk: () => new AbstractRegion(), // todo add chunk region
    sphere: () => new AbstractRegion(), // todo add sphere region
    cylinder: () => new AbstractRegion(), // todo add cylinder region
    void: () => new AbstractRegion(), // todo add void region
  },

  /**
   * Maps the object to the instance of the object
   *
   * @param {string} obj
   * @return {AbstractRegion}
   */
  map_region: (obj) => {
    const key = _.first(_.keys(obj))
    const value = obj[key]
    if (Regions.REGION_CONSTRUCT[key]) {
      return Regions.REGION_CONSTRUCT[key](value)
    }
    throw new Error(`There was no REGION_CONSTRUCT for ${key}`)
  },
}

export default Regions
