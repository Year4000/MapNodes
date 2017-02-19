/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */

/** Represents a kit from the json object */
class Clazz extends JsonObject {

  constructor(id, clazz) {
    super(id, clazz);
  }

  /** Get the json for this clazz */
  get clazz() {
    return super._json;
  }
}