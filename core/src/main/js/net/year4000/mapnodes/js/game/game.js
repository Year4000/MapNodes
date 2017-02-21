/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */

/**
 The class that is the game
 */
class Game {
  constructor(id, map) {
    this._id = Conditions.not_null(id, 'id');
    this._map = Conditions.not_null(map, 'map');
  }

  get id() {
    return this._id;
  }

  /** When a user or team has won the game */
  winner(winner) {
    if (winner instanceof Player) {
      $.end_game_player(winner.uuid);
    } else if (winner instanceof Team) {
      $.end_game_team(winner.id);
    }
  }

  /** Load the game from the JSON object */
  load() {
    println(`The game(${this._id}) is loading...`);
  }

  /** Start the game and unload the previous game */
  start() {
    println(`The game(${this._id}) has started...`);
  }

  /** Stop the game and get ready to load the next game */
  stop() {
    println(`The game(${this._id}) has stopped...`);
  }

  /** Unload the game, clean things up for the next game */
  unload() {
    println(`The game(${this._id}) has been unloaded...`);
  }

  /** Register the team into the system */
  _register_team(team_id, team) {
    Conditions.not_null(team_id, 'team_id');
    Conditions.is_object(team, 'team');
    // todo register the team and inject things
  }

  /** Register the class into the system */
  _register_class(class_id, clazz) {
    Conditions.not_null(class_id, 'class_id');
    Conditions.is_object(clazz, 'clazz');
    // todo register the class and inject things
  }

  /** Register the kit into the system */
  _register_kit(kit_id, kit) {
    Conditions.not_null(kit_id, 'kit_id');
    Conditions.is_object(kit, 'kit');
    // todo register the kit and inject things
  }

  /** Register the region into the system */
  _register_region(region_id, region) {
    Conditions.not_null(region_id, 'region_id');
    Conditions.is_object(region, 'region');
    // todo register the region and inject things
  }
}