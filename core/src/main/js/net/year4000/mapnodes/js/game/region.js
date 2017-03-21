/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */

/** Represents a region from the json object */
class Region extends JsonObject {

  constructor(id, region) {
    super(id, region)
    this._regions = _.map(region.zones, zone => Regions.map_region(zone))
  }

  /** Get the json for this region */
  get region() {
    return this._json
  }
}
