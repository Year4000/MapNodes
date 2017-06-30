/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
'use strict'

/** Represents a game from the json object */
class Game extends JsonObject {

  constructor(id, map) {
    super(id, _.merge(JSON.parse(JSON.stringify(Game.DEFAULT_MAP)), map))
    this._teams = Immutable.Map()
    this._kits = Immutable.Map()
    this._regions = Immutable.Map()
    this._clazzes = Immutable.Map()
    this._state = 'WAITING'
    this._players = []
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
      spawns: this.map.world.spawn
    })
    // Register instances controlled by the map
    _.forEach(this.map.teams, (json, id) => this._register_team(id, json))
    _.forEach(this.map.kits, (json, id) => this._register_kit(id, json))
    _.forEach(this.map.regions, (json, id) => this._register_region(id, json))
    _.forEach(this.map.classes, (json, id) => this._register_class(id, json))
    Logger.info('Finish registering map components')
  }

  /** Check if the game is running */
  is_running() {
    return this._state === 'RUNNING'
  }

  /** Load the game from the JSON object */
  load() {
    Logger.info(`The game(${this._id}) is loading...`)
    this.$event_emitter.trigger('game_load', [this])
  }

  /** Start the game and unload the previous game */
  start() {
    this._state = 'RUNNING'
    this._start_time = _.now()
    Logger.info(`The game(${this._id}) has started as ${moment(this._start_time).format('l LTS')}...`)
    this.$event_emitter.trigger('game_start', [this])
    for (let team of this._teams.values()) { // todo filter out spectators
      team.start()
    }
  }

  /** Stop the game and get ready to load the next game */
  stop() {
    this._state = 'ENDED'
    this._stop_time = _.now()
    Logger.info(`The game(${this._id}) has stopped as ${moment(this._stop_time).format('l LTS')}...`)
    this.$event_emitter.trigger('game_stop', [this])
    for (let player of this._players) {
      this.$event_emitter.trigger('stop_game_player', [player, this])
      player.stop()
    }
  }

  /** Cycle to the next game */
  cycle(next_game) {
    Conditions.not_null(next_game, 'next_game')
    // Have all the players leave the game
    _.forEach(this._players, player => {
      this._leave_game(player)
      next_game._join_game(player)
    })
    this.$event_emitter.trigger('game_cycle', [next_game, this])
  }

  /** Unload the game, clean things up for the next game */
  unload() {
    Logger.info(`The game(${this._id}) has been unloaded...`)
    this.$event_emitter.trigger('game_unload', [this])
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

  /** Get the tablist header*/
  get tablist_header() {
    return `${this.state_color}${this.title_color}`
  }

  /** Join the player to the game, player should be a uuid but can be a username */
  join_game(player) {
    Conditions.not_null(player, 'player')
    this._join_game(Player.of(player))
  }

  /** Actually have the player join the game */
  _join_game(player) {
    Conditions.not_null(player, 'player')
    this.$injector.inject_instance(player)
    this._players.push(player)
    let spectator = this._teams.get(Facts.SPECTATOR_ID)
    spectator.join(player)
    player._current_team = spectator
    this.$event_emitter.trigger('join_game', [player, this])
    player.teleport(...this.spawn_point.toArray())
  }

  /** Clean up the player, player should be a uuid but can be a username */
  leave_game(player) {
    Conditions.not_null(player, 'player')
    let player_object = Player.of(player)
    this._leave_game(_.find(this._players, object => object.is_equal(player_object)))
  }

  /** Actually have the player leave the game */
  _leave_game(player) {
    player.leave_team()
    _.remove(this._players, player)
    this.$event_emitter.trigger('leave_game', [player, this])
  }

  /** Generate the Team object containing the least about of players */
  get _smallest_team() {
    return this._teams.filterNot(team => team.id == Facts.SPECTATOR_ID).sortBy(team => team.size).first()
  }

  /** The abstraction to register the object */
  _register(obj_id, obj_json, clazz, collection_name, event_id) {
    Conditions.not_null(obj_id, 'obj_id')
    Conditions.not_null(obj_json, 'obj_json')
    Logger.info(`Registering ${collection_name} with id ${obj_id}`)
    let obj = new clazz(obj_id, obj_json)
    this.$injector.inject_instance(obj)
    this[collection_name] = this[collection_name].set(obj_id, obj)
    this.$event_emitter.trigger(event_id, [obj, this])
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

  /** Get the value of this game */
  // Override
  valueOf() {
    return `id: ${this.id} title: ${this.title}`
  }
}

/** The defaults for the map */
Game.DEFAULT_MAP = {
  map: {
    name: 'Unknown',
    version: '0.1',
    descripton: 'Unknown Map',
  },
}

/** The colors for each game state */
Game._STATE_COLORS = {WAITING: '&e', RUNNING: '&a', ENDED: '&c'}
