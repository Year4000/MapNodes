/*
 * Copyright 2019 Year4000. All Rights Reserved.
 */
import JsonObject from './json_object.js'
import Player from './player.js'

/** Represents the map settings from the json object */
export default class Map extends JsonObject {

  /** This follows the documented scheme here https://resources.year4000.net/mapnodes/map_component */
  static get schema() {
    return {
      name: { type: 'string' },
      version: { type: 'string' },
      description: { type: 'string' },
      author: { type: 'string' },
      authors: { type: 'array' },
    }
  }

  /** Get the json for this clazz */
  get map() {
    return this._json
  }

  /** Get all the authors of this map */
  get authors() {
    // todo handle offline players?
    // Get the single author of the map
    if (this._json.author) {
      return [ Player.of(this._json.author) ]
    }
    // Get all the authors of the map
    if (this._json.authors) {
      return this._json.authors.map(author => Player.of(author))
    }
    return []
  }
}
