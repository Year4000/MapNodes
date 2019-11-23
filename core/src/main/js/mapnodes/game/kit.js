/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
'use strict'

/** Represents a kit from the json object */
class Kit extends JsonObject {

  constructor(id, kit) {
    super(id, kit)
  }

  /** Get the json for this kit */
  get kit() {
    return this._json
  }
}
