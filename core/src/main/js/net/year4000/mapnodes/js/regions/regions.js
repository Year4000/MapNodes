/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */

/** The known regions the system knows how to handle */
const Regions = {
  /** The map from key to instance type */
  REGION_MAP: {
    point: PointRegion,
    cube: CubeRegion,
    cuboid: CuboidRegion,
    global: GlobalRegion,
  },
}
