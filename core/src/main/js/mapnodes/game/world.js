/*
 * Copyright 2019 Year4000. All Rights Reserved.
 */
import JsonObject from './json_object.js'

/** Represents the world from the json object */
export default class World extends JsonObject {

  /** This follows the documented scheme here https://resources.year4000.net/mapnodes/world_component */
  static get schema() {
    return {
      spawn: { type: 'string' },
      spawns: { type: 'array' },
      difficulty: { type: ['number', 'string'] },
      weather: { type: 'boolean' },
      time_lock: { type: ['number', 'string'] },
      environment: { type: 'string' },
      world_height: { type: 'number' },
      resource_pack: { type: 'string' },
    }
  }

  /** Get the json for this clazz */
  get world() {
    return this._json
  }
}
