/*
 * Copyright 2020 Year4000. All Rights Reserved.
 */
import Logger from 'js-logger'

import JsonObject from '../game/json_object.js'


/**
 * This class abstracts a game mode to be used in the system
 */
export default class AbstractGameMode extends JsonObject {
  constructor(json) {
    super('this.constructor.$id', json)
    this._id = this.constructor.$id
  }

  /**
   * Loads the game mode and registers the handlers.
   * This is called when the map is loaded.
   */
  _load() {
    Logger.info(`Verifying that the schema matches the game settings for ${this.id}`)
    if (!this.verify()) {
      throw new Error('Game Mode has bad config options')
    }
    return this.load?.() // todo make theses events that the game mode will listen to
  }

  /**
   * Enables the game mode.
   * This is called after the map has been loaded, and the game has started.
   */
  _enable() {
    Logger.info(`Enabling ${this.id}`)
    return this.enable?.() // todo make theses events that the game mode will listen to
  }

  /**
   * Disables the game mode.
   * This is called when the winner is decided, or when the game has stopped.
   */
  _disable() {
    Logger.info(`Disabling ${this.id}`)
    return this.disable?.() // todo make theses events that the game mode will listen to
  }

  /**
   * Unload the game mode.
   * This is called last and when its time to do clean up on the game mode.
   */
  _unload() {
    Logger.info(`Disabling ${this.id}`)
    return this.unload?.() // todo make theses events that the game mode will listen to
  }
}
