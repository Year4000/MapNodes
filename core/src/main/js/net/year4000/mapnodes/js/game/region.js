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
    return super._json
  }
}
