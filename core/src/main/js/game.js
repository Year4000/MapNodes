/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */
class Game {
  constructor(id) {
    this._id = id;
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