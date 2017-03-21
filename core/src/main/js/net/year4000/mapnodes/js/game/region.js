/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */

/** Represents a region from the json object */
class Region extends JsonObject {

  constructor(id, region) {
    super(id, region)
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
