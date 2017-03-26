/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
'use strict'

/** Represents a game mode */
class GameMode extends JsonObject {

  constructor(id, json) {
    super(id, json)
  }

  /** Get the json for this kit */
  game_mode() {
    return this._json
  }
}
