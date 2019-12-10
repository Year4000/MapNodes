/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
import JsonObject from './json_object.js'

/** Represents a game mode */
export default class GameMode extends JsonObject {

  constructor(id, json) {
    super(id, json)
  }

  /** Get the json for this kit */
  game_mode() {
    return this._json
  }
}
