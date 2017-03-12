/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */

/** Represents a game from the json object */
class Game extends JsonObject {

  constructor(id, map) {
    super(id, map)
    this._teams = Immutable.Map()
    this._kits = Immutable.Map()
    this._regions = Immutable.Map()
    this._clazzes = Immutable.Map()
    this._state = 'WAITING'
    this._players = []
    println(`Constructing the game id ${id}`)
  }

  /** Get the json for this map */
  get map() {
    return this._map
  }

  /** Register the things the map has */
  register_map() {
    let game = this
    _.forEach(this._json.teams, (json, id) => game._register_team(id, json))
    _.forEach(this._json.kits, (json, id) => game._register_kit(id, json))
    _.forEach(this._json.regions, (json, id) => game._register_region(id, json))
    _.forEach(this._json.classes, (json, id) => game._register_class(id, json))
  }

  /** Check if the game is running */
  is_running() {
    return this._state === 'RUNNING'
  }

  /** Load the game from the JSON object */
  load() {
    println(`The game(${this._id}) is loading...`)
    this.$event_emitter.trigger('game_load', [this])
  }

  /** Start the game and unload the previous game */
  start() {
    println(`The game(${this._id}) has started...`)
    this._state = 'RUNNING'
    this.$event_emitter.trigger('game_start', [this])
  }

  /** Stop the game and get ready to load the next game */
  stop() {
    println(`The game(${this._id}) has stopped...`)
    this._state = 'ENDED'
    this.$event_emitter.trigger('game_stop', [this])
  }

  /** Unload the game, clean things up for the next game */
  unload() {
    println(`The game(${this._id}) has been unloaded...`)
    this.$event_emitter.trigger('game_unload', [this])
  }

  /** Join the player to the game */
  join_game(player) {
    Conditions.not_null(player, 'player')
    let player_object = Player.of(player)
    this._players.push(player_object)
    //this._smallest_team.join(player_object) todo join spectator team
    this.$event_emitter.trigger('join_game', [player_object, this])
  }

  /** Generate the Team object containing the least about of players */
  get _smallest_team() {
    return _(this._teams).sortBy('size').last()
  }

  /** The abstraction to register the object */
  _register(obj_id, obj_json, clazz, collection_name, event_id) {
    Conditions.not_null(obj_id, 'obj_id')
    Conditions.not_null(obj_json, 'obj_json')
    println(`Registering ${collection_name} with id ${obj_id}`)
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
}