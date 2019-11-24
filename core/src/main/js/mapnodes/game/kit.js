/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
import JsonObject from './json_object.js'
import { inject } from '../injection.js'
import Game from './game.js'

/** Represents a kit from the json object */
export default class Kit extends JsonObject {

  @inject(Game) $game

  constructor(id, kit) {
    super(id, kit)
  }

  /** Get the json for this kit */
  get kit() {
    return this._json
  }
}
