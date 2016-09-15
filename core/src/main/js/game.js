/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */

/**
 The class that is the game
 */
class Game {
  constructor(id, map) {
    this._id = id;
    this._map = map;
  }

  get id() {
    return this._id;
  }

  end(winner) {
    if (winner instanceof Player) {
      $.end_game_player(winner.uuid);
    } else if (winner instanceof Team) {
      $.end_game_team(winner.id);
    }
  }
}