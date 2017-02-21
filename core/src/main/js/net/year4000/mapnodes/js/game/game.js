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
}