/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */

/** Generates the player object */
class Player {

  constructor(json) {
    this._meta = json;
  }

  /** Create the instance of this player */
  static of(player) {
    Conditions.not_null(player, 'player');
    Conditions.is_true(typeof player === 'string');
    if (_.size(player) > Facts.MAX_USERNAME_SIZE) { // must be uuid
      return Player.of_uuid(player);
    } else { // Must be username
      return Player.of_username(player);
    }
  }

  /** Generate the player meta from the uuid */
  static of_uuid(uuid) {
    Conditions.not_null(uuid, 'uuid');
    return new Player($.bindings.player_meta_uuid(uuid));
  }

  /** Generate the player meta from a username */
  static of_username(username) {
    Conditions.not_null(username, 'username');
    return new Player($.bindings.player_meta_username(username));
  }

  get uuid() {
    return this._meta.uuid;
  }

  get username() {
    return this._meta.username;
  }

  /** Send a message to this player */
  send_message(message) {
    $.bindings.send_message(this.uuid, message);
  }
}
