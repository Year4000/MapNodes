/*
 * Copyright 2020 Year4000. All Rights Reserved.
 */
import JsonObject from './json_object.js'

/** Represents a kit from the json object */
export default class Kit extends JsonObject {
  /** This follows the documented scheme here https://resources.year4000.net/mapnodes/kits_component */
  static get schema() {
    return {
      gamemode: { type: 'string' },
      fly: { type: 'boolean' },
      health: { type: 'number' },
      food: { type: 'number' },
      permissions: { type: 'array' },
      armor: { type: 'object' },
      items: { type: 'array' },
      effects: { type: 'array' },
    }
  }

  /** Get the json for this kit */
  get kit() {
    return this._json
  }
}
