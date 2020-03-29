/*
 * Copyright 2020 Year4000. All Rights Reserved.
 */
import JsonObject from './json_object.js'


/** Represents a game mode */
export default class GameMode extends JsonObject {
  /** Get the json for this kit */
  game_mode() {
    return this._json
  }
}
