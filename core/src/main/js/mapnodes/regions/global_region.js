/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
import AbstractRegion from './abstract_region.js'

/** A region that contains everything */
export default class GlobalRegion extends AbstractRegion {
  contains(vector3) {
    return true
  }
}
