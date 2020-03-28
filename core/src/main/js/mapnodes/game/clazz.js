/*
 * Copyright 2020 Year4000. All Rights Reserved.
 */
import JsonObject from './json_object.js'

/** Represents a class from the json object */
export default class Clazz extends JsonObject {
  /** This follows the documented scheme here https://resources.year4000.net/mapnodes/map_component */
  static get schema() {
    return {
      name: { type: 'string' },
      kit: { type: 'string' },
    }
  }

  /** Get the json for this clazz */
  get clazz() {
    return this._json
  }
}
