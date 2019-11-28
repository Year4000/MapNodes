/*
 * Copyright 2019 Year4000. All Rights Reserved.
 */
import Logger from 'js-logger'
import { not_null } from '../conditions.js'
import { inject } from '../injection.js'

/** The registry of the game modes */
export default class GameRegistry {
  @inject() injector
  game_modes = {}

  /**
   * Register the id and the type that will be created in time
   *
   * @param id The id of the game mode
   * @param game_mode The class type of the game mode
   */
  register(id, game_mode) {
    not_null(id, 'id must not be null')
    not_null(game_mode, 'game_mode id must not be null')
    this.game_modes[id] = game_mode
  }

  /**
   * Create the instance of the game, construct the instance with the json config.
   * Then injects the instance with the injector
   *
   * @param id The id of the game mode
   * @param json The optional json config object
   */
  create(id, json = {}) {
    not_null(id, 'id must not be null')
    try {
      let instance = new this.game_modes[id](json)
      return this.injector.inject_instance(instance)
    } catch (e) {
      Logger.error(`There was a problem constructing the game mode with id: ${id}`)
      Logger.error(e)
    }
  }

  /**
   * Check if the game mode is registered in the system
   *
   * @param id The id of the game mode
   */
  is_registered(id) {
    return id in this.game_modes
  }

  /**
   * Get the collection of registered ids
   * @return Array
   */
  get registered_ids() {
    return Object.keys(this.game_modes)
  }
}

/** The singleton of the game registry system */
export const game_registry = new GameRegistry()

/** The game mode decorator that will register the class into the system */
export const game_mode = id => handler => ({
  ...handler,
  elements: [...handler.elements, {
    kind: 'field',
    placement: 'static',
    key: '$id',
    descriptor: { writable: false },
    initializer: () => id, // inject the id that was used when registering
  }, {
    kind: 'field',
    placement: 'static',
    key: '$game_registry',
    descriptor: {},
    initializer: function() {
      // Use this to get the type
      return game_registry.register(id, this)
    },
  }],
})
