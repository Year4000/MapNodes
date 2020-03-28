/*
 * Copyright 2020 Year4000. All Rights Reserved.
 */
import Logger from 'js-logger'
import _ from 'lodash'
import moment from 'moment'
import { Map } from 'immutable'
import Facts from '../facts.js'
import Clazz from './clazz.js'
import JsonObject from './json_object.js'
import Kit from './kit.js'
import Player from './player.js'
import Region from './region.js'
import Team from './team.js'
import { not_null } from '../conditions.js'
import { inject } from '../injection.js'

/** Represents a game from the json object */
export default class Game extends JsonObject {
  @inject() event_manager
  @inject() injector
  @inject() game_registry

  _teams = Map()
  _kits = Map()
  _regions = Map()
  _clazzes = Map()
  _events = Map()
  _games = {}
  _state = 'WAITING'
  _players = []

  /** This follows the documented scheme here https://resources.year4000.net/mapnodes/map_component */
  static get schema() {
    return {
      name: { type: 'string' },
      version: { type: 'string' },
      description: { type: 'string' },
      authors: { type: 'array' },
    }
  }

  constructor(id, map) {
    super(id, { ...Game.DEFAULT_MAP, ...map })
    Logger.info(`Constructing the game ${id} for ${this._json.map.name}`)
  }

  /** Get the json for this map */
  get map() {
    return this._json
  }

  /** Register the things the map has */
  register_map() {
    // Register internal instances
    this._register_kit(Facts.SPECTATOR_ID, {})
    this._register_team(Facts.SPECTATOR_ID, {
      name: 'Spectator',
      color: 'gray',
      kit: Facts.SPECTATOR_ID,
      size: -1,
      spawns: this.map.world.spawn,
    })
    // Register instances controlled by the map
    _.forEach(this.map.teams, (json, id) => this._register_team(id, json))
    _.forEach(this.map.kits, (json, id) => this._register_kit(id, json))
    _.forEach(this.map.regions, (json, id) => this._register_region(id, json))
    _.forEach(this.map.classes, (json, id) => this._register_class(id, json))
    _.forEach(this.map.events, (json, id) => this._register_event(id, json))
    _.forEach(this.map.games, (json, id) => this._register_game(id, json))
    Logger.info('Finish registering map components')
    // start the game auto
    if (this.settings.start_game_on_load) {
      Logger.info('Starting the game right now because the map overrides this')
      this.start()
    }
  }

  /** Check if the game is running */
  is_running() {
    return this._state === 'RUNNING'
  }

  /** Load the game from the JSON object */
  load() {
    Logger.info(`The game(${this._id}) is loading...`)
    this.event_manager.trigger('game_load', [this])
    Logger.info('Loading the game mode')
    _.forEach(this._games, (game) => game._load())
  }

  /** Start the game and unload the previous game */
  start() {
    this._state = 'RUNNING'
    this._start_time = _.now()
    Logger.info('Enabling the game modes')
    _.forEach(this._games, (game) => game._enable())
    Logger.info(`The game(${this._id}) has started as ${moment(this._start_time).format('l LTS')}...`)
    this.event_manager.trigger('game_start', [this])
    for (const team of this._teams.values()) {
      team.start() // spectators and players should be handle differently
    }
  }

  /** Stop the game and get ready to load the next game */
  stop() {
    this._state = 'ENDED'
    this._stop_time = _.now()
    Logger.info(`The game(${this._id}) has stopped as ${moment(this._stop_time).format('l LTS')}...`)
    this.event_manager.trigger('game_stop', [this])
    for (const player of this._players) {
      this.event_manager.trigger('stop_game_player', [player, this])
      player.stop()
    }
    Logger.info('Disabling the game modes')
    _.forEach(this._games, (game) => game._disable())
    this.unload() // unload things in the game
  }

  /** Cycle to the next game */
  cycle(next_game) {
    not_null(next_game, 'next_game')
    // Have all the players leave the game
    _.forEach(this._players, (player) => {
      this._leave_game(player)
      next_game._join_game(player)
    })
    this.event_manager.trigger('game_cycle', [next_game, this])
  }

  /** Unload the game, clean things up for the next game */
  unload() {
    Logger.info(`The game(${this._id}) has been unloaded...`)
    this.event_manager.trigger('game_unload', [this])
    _.forEach(this.map.events, (json, id) => this._unregister_event(id, json))
    Logger.info('Unloading the game modes')
    _.forEach(this._games, (game) => game._unload())
  }

  /** Get the proxy object for the map_nodes object in the map, function are called and return the value */
  get settings() {
    if (!this._settings) {
      this._settings = new Proxy(this.map.map_nodes, {
        get: (target, name) => {
          if (name in target) {
            const variable = target[name]
            if (typeof variable === 'function') {
              try {
                return variable()
              } catch (any) {
                Logger.error(any)
              }
            } else {
              return variable
            }
          }
          return null
        },
      })
    }
    return this._settings
  }

  /** Get the time the game has started, -1 if hasent started yet */
  get start_time() {
    return this._start_time || -1
  }

  /** Get the time the game has stopped, -1 if hasent stoped yet */
  get stop_time() {
    return this._stop_time || -1
  }

  /** Get the delta time the game has been running for */
  get game_time() {
    return _.now() - this.start_time
  }

  /** Get a random point from the list of spawns by the spectator team */
  get spawn_point() {
    return this._teams.get(Facts.SPECTATOR_ID).spawn_point
  }

  /** Get the title of the game */
  get title() {
    return `${this.map.map.name} ${this.map.map.version}`
  }

  /** Get the title of the game */
  get title_color() {
    return `${this.map.map.name} &7${this.map.map.version.replace(/([.])/, '&8$1&7')}`
  }

  /** Get the color of the current game state */
  get state_color() {
    return Game._STATE_COLORS[this._state] || '&4'
  }

  /** Get the tablist header use map override if needed */
  get tablist_header() {
    return this.settings.tab_list_header || `${this.state_color}${this.title_color}`
  }

  /** Join the player to the game, player should be a uuid but can be a username */
  join_game(player) {
    not_null(player, 'player')
    this._join_game(Player.of(player))
  }

  /** Actually have the player join the game */
  _join_game(player) {
    not_null(player, 'player')
    this.injector.inject_instance(player)
    this._players.push(player)
    if (this.settings.join_game_on_join) { // should players auto join the game when they login
      player.join_team(this._smallest_team)
      this.event_manager.trigger('join_game', [player, this])
      player.start()
    } else {
      const spectator = this._teams.get(Facts.SPECTATOR_ID)
      spectator.join(player)
      player._current_team = spectator
      this.event_manager.trigger('join_game', [player, this])
      player.teleport(...this.spawn_point.toArray())
    }
  }

  /** Clean up the player, player should be a uuid but can be a username */
  leave_game(player) {
    not_null(player, 'player')
    const player_object = Player.of(player)
    this._leave_game(_.find(this._players, (object) => object.is_equal(player_object)))
  }

  /** Actually have the player leave the game */
  _leave_game(player) {
    player.leave_team()
    _.remove(this._players, player)
    this.event_manager.trigger('leave_game', [player, this])
  }

  /** Generate the Team object containing the least about of players */
  get _smallest_team() {
    return this._teams.filterNot((team) => team.id === Facts.SPECTATOR_ID).sortBy((team) => team.size).first()
  }

  /** The abstraction to register the object */
  _register(obj_id, obj_json, Clazz, collection_name, event_id) {
    not_null(obj_id, 'obj_id')
    not_null(obj_json, 'obj_json')
    Logger.info(`Registering ${collection_name} with id ${obj_id}`)
    const obj = new Clazz(obj_id, obj_json)
    this.injector.inject_instance(obj)
    this[collection_name] = this[collection_name].set(obj_id, obj)
    this.event_manager.trigger(event_id, [obj, this])
  }

  /** Register the team into the system */
  _register_team(team_id, team_json) {
    this._register(team_id, team_json, Team, '_teams', 'register_team')
  }

  /** Register the class into the system */
  _register_class(class_id, clazz_json) {
    this._register(class_id, clazz_json, Clazz, '_clazzes', 'register_class')
  }

  /** Register the kit into the system */
  _register_kit(kit_id, kit_json) {
    this._register(kit_id, kit_json, Kit, '_kits', 'register_kit')
  }

  /** Register the region into the system */
  _register_region(region_id, region_json) {
    this._register(region_id, region_json, Region, '_regions', 'register_region')
  }

  /** Register the event into the system */
  _register_event(event_id, event_function) {
    not_null(event_id, 'event_id')
    not_null(event_function, 'event_function')
    Logger.info(`Registering event type ${event_id}`)
    if (typeof event_function !== 'function') {
      Logger.error('Event is not a function')
      return
    }
    const wrapper = (function(...args) {
      try {
        if (!wrapper.$compiled) {
          wrapper.$compiled = eval(String(this.events[event_id])) // eslint-disable-line no-eval
        }
        wrapper.$compiled(...args) // run the function in the context of this wrapper
      } catch (any) {
        Logger.error(any)
      }
    }).bind(this.map)
    this._events = this._events.set(event_id, wrapper)
    this.event_manager.on(event_id, wrapper)
  }

  /** Register the game into the system */
  _register_game(game_id, json) {
    not_null(game_id, 'game_id')
    not_null(json, 'event_function')
    if (this.game_registry.is_registered(game_id)) {
      Logger.info(`Registering game type ${game_id}`)
      this._games[game_id] = this.game_registry.create(game_id, json)
    } else if (typeof json === 'function') {
      Logger.info(`Creating custom game type ${game_id}`)
      // todo register the custom game mode type
    } else {
      Logger.warn(`Game mode ${game_id} is not a registered game mode nor is a class`)
    }
  }

  /** Unregister the event into the system */
  _unregister_event(event_id, event_function) {
    not_null(event_id, 'event_id')
    not_null(event_function, 'event_function')
    const listener = this._events.get(event_id)
    this.event_manager.off(event_id, listener)
  }

  /** Get the value of this game */
  // Override
  valueOf() {
    return `id: ${this.id} title: ${this.title}`
  }

  /** The defaults for the map */
  static DEFAULT_MAP = {
    map: {
      name: 'Unknown',
      version: '0.1',
      description: 'Unknown Map',
    },

    // Settings that maps or games can override
    map_nodes: {
      // The header for the tab list to override the system one
      tab_list_header: null,
      // The footer for the tab list
      tab_list_footer: '&3[&bYear4000&3] &7- &bmc&3.&byear4000&3.&bnet',
      // Do players join the game when they enter the server
      join_game_on_join: false,
      // Does the game start when the map is loaded
      start_game_on_load: false,
    },
  }

  /** The colors for each game state */
  static _STATE_COLORS = { WAITING: '&e', RUNNING: '&a', ENDED: '&c' }
}
