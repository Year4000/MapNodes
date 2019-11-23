/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
import JsonObject from './json_object.js'

/** Represents a kit from the json object */
export default class Clazz extends JsonObject {

  constructor(id, clazz) {
    super(id, clazz)
  }

  /** Get the json for this clazz */
  get clazz() {
    return this._json
  }
}
