/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */

/** Represents a game from the json object */
class Game extends JsonObject {
  constructor(id, map) {
    super(id, map);
    this._teams = Immutable.Map();
    this._kits = Immutable.Map();
    this._regions = Immutable.Map();
    this._clazzes = Immutable.Map();
    println(`Construct the game id ${id}`);
  }

  /** Get the json for this map */
  get map() {
    return this._map;
  }

  /** Load the game from the JSON object */
  load() {
    println(`The game(${this._id}) is loading...`);
    this.$event_emitter.trigger('game_load', [this]);
  }

  /** Start the game and unload the previous game */
  start() {
    println(`The game(${this._id}) has started...`);
    this.$event_emitter.trigger('game_start', [this]);
  }

  /** Stop the game and get ready to load the next game */
  stop() {
    println(`The game(${this._id}) has stopped...`);
    this.$event_emitter.trigger('game_stop', [this]);
  }

  /** Unload the game, clean things up for the next game */
  unload() {
    println(`The game(${this._id}) has been unloaded...`);
    this.$event_emitter.trigger('game_unload', [this]);
  }

  /** The abstraction to register the object */
  _register(obj_id, obj_json, clazz, collection_name, event_id) {
    Conditions.not_null(obj_id, 'obj_id');
    Conditions.is_object(obj_json, 'obj_json');
    let obj = new clazz(obj_id, obj_json);
    this[collection_name] = this[collection_name].set(obj_id, obj);
    this.$event_emitter.trigger(event_id, [obj, this]);
  }

  /** Register the team into the system */
  _register_team(team_id, team_json) {
    this._register(team_id, team_json, Team, '_teams', 'register_team');
  }

  /** Register the class into the system */
  _register_class(class_id, clazz_json) {
    this._register(class_id, clazz_json, Clazz, '_clazzes', 'register_class');
  }

  /** Register the kit into the system */
  _register_kit(kit_id, kit_json) {
    this._register(kit_id, kit_json, Kit, '_kits', 'register_kit');
  }

  /** Register the region into the system */
  _register_region(region_id, region_json) {
    this._register(region_id, region_json, Region, '_regions', 'register_region');
  }
}