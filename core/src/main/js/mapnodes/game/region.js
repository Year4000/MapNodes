/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
import _ from 'lodash'
import JsonObject from './json_object.js'
import Regions from '../regions/regions.js'

/** Represents a region from the json object */
export default class Region extends JsonObject {

  /** This follows the documented scheme here https://resources.year4000.net/mapnodes/regions_component */
  static get schema() {
    return {
      events: { type: 'object' },
      zones: { type: 'array' },
    }
  }

  /** Get the json for this region */
  get region() {
    return this._json
  }

  /** Lazy load all zone regions */
  get zones() {
    return this._zones || (this._zones = _.map(this.region.zones, zone => Regions.map_region(zone)))
  }
}
