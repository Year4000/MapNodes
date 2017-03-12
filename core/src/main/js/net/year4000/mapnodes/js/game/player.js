/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */

/** Generates the player object */
class Player {

  constructor(username, uuid) {
    Conditions.not_null(username, 'username')
    Conditions.not_null(uuid, 'uuid')
    this._meta = {username: username, uuid: uuid}
    this._current_team = null;
  }

  /** Create the instance of this player */
  static of(player) {
    Conditions.not_null(player, 'player')
    Conditions.is_true(typeof player === 'string')
    if (_.size(player) > Facts.MAX_USERNAME_SIZE) { // must be uuid
      return Player.of_uuid(player)
    } else { // Must be username
      return Player.of_username(player)
    }
  }

  /** Generate the player meta from the uuid */
  static of_uuid(uuid) {
    Conditions.not_null(uuid, 'uuid')
    let meta = $.bindings.player_meta_uuid(uuid).split(':')
    return new Player(meta[0], meta[1])
  }

  /** Generate the player meta from a username */
  static of_username(username) {
    Conditions.not_null(username, 'username')
    let meta = $.bindings.player_meta_username(username).split(':')
    return new Player(meta[0], meta[1])
  }

  get uuid() {
    return this._meta.uuid
  }

  get username() {
    return this._meta.username
  }

  /** Update the team refrence for the player */
  set _team(team) {
    Conditions.not_null(team, 'team')
    this._current_team = team;
  }

  /** Send a message to this player */
  send_message(message) {
    $.bindings.send_message(this.uuid, message || '')
  }

  /** Have the player leave the team they are on */
  leave() {
    if (this._current_team) {
      this._current_team.leave(this)
    }
  }

  /** Checks if the two player objects are equal */
  is_equal(player) {
    return typeof player === 'object' && this.uuid === player.uuid
  }
}
