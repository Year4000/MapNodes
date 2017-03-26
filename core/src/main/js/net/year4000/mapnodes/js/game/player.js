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
    return new Player(...$.bindings.player_meta_uuid(uuid).split(':'))
  }

  /** Generate the player meta from a username */
  static of_username(username) {
    Conditions.not_null(username, 'username')
    return new Player(...$.bindings.player_meta_username(username).split(':'))
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

  /** Set the header for the tablist */
  set tablist_header(header) {
    $.bindings.tablist_header(this.uuid, header)
  }

  teleport(x, y, z, yaw, pitch) {
    $.bindings.teleport(this.uuid, x, y, z, yaw || 0, pitch || 0)
  }

  /** Start the game for the player */
  start() {
    this.$event_emitter.trigger('start_player', [this, this.$game])
  }

  /** Stop the game for the player */
  stop() {
    this.leave()
  }

  /** Have the player leave the game they are on */
  leave() {
    this.$event_emitter.trigger('stop_player', [this, this.$game])
    if (this._current_team) {
      this._current_team.leave(this)
    }
  }

  /** Checks if the two player objects are equal */
  is_equal(player) {
    return typeof player === 'object' && this.valueOf() === player.valueOf()
  }

  /** Get the value of this player */
  // Override
  valueOf() {
    return this.uuid
  }
}
