/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */
import _ from 'lodash'
import EventEmitter from 'wolfy87-eventemitter'
import { inject } from '../injection.js'
import { not_null, is_true } from '../conditions.js'
import Facts from '../facts.js'
import Messages from '../messages.js'
import Game from './game.js'

/** The instance of all the player that has ever joined this server's instance */
const _player_instances = {}

/** Generates the player object */
export default class Player {

  @inject(EventEmitter) $event_emitter
  @inject(Game) $game

  constructor(username, uuid) {
    not_null(username, 'username')
    not_null(uuid, 'uuid')
    _player_instances[uuid] = this
    _player_instances[username] = this // todo there may be a name clash, should add some checks later
    this._meta = { username, uuid }
    this._current_team = null;
  }

  /** Create the instance of this player */
  static of(player) {
    not_null(player, 'player')
    is_true(typeof player === 'string')
    if (player in _player_instances) { // check the cache first
      return _player_instances[player]
    }
    if (_.size(player) > Facts.MAX_USERNAME_SIZE) { // must be uuid
      return Player.of_uuid(player)
    } else { // Must be username
      return Player.of_username(player)
    }
  }

  /** Generate the player meta from the uuid */
  static of_uuid(uuid) {
    not_null(uuid, 'uuid')
    return new Player(...$.bindings.player_meta_uuid(String(uuid)).split(':'))
  }

  /** Generate the player meta from a username */
  static of_username(username) {
    not_null(username, 'username')
    return new Player(...$.bindings.player_meta_username(String(username)).split(':'))
  }

  get uuid() {
    return this._meta.uuid
  }

  get username() {
    return this._meta.username
  }

  /** Send a message to this player */
  send_message(message) {
    $.bindings.send_message(this.uuid, String(message || ''))
  }

  /** Check if the player has the permission */
  has_permission(permission) {
    return $.bindings.has_permission(this.uuid, String(permission))
  }

  /** Set the header for the tablist */
  set tablist_header(header) {
    $.bindings.tablist_header(this.uuid, String(header))
  }

  /** Set the header for the tablist */
  set tablist_footer(footer) {
    $.bindings.tablist_footer(this.uuid, String(footer))
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
    this.$event_emitter.trigger('stop_player', [this, this.$game])
    this.leave_team()
  }

  /** Check if the player is playing */
  is_playing() {
    return this._current_team && this._current_team.id !== Facts.SPECTATOR_ID && this.$game.is_running()
  }

  /** Check if the player is playing */
  is_spectating() {
    return this._current_team && this._current_team.id === Facts.SPECTATOR_ID
  }

  /** Have the player join the specific team */
  join_team(team) {
    not_null(team, 'team')
    if (this.$game.is_running() && this.is_playing()) {
      return Messages.TEAM_MENU_NOT_NOW.send(this)
    }
    if (this.$game.is_running() && this.is_spectating()) {
      this.leave_team()
      team.join(this)
      this._current_team = team;
      this.start()
      return
    }
    team.join(this)
    this._current_team = team;
  }

  /** Have the player leave the game they are on */
  leave_team() {
    if (this._current_team) {
      this._current_team.leave(this)
      this._current_team = null
    }
  }

  /** Get the current team for the player, this should always return something */
  get team() {
    return this._current_team
  }

  /** Checks if the two player objects are equal */
  is_equal(player) {
    return typeof player === 'object' && this.valueOf() == player.valueOf() // only use == since the strings are not exactly the same string
  }

  /** Get the value of this player */
  // Override
  valueOf() {
    return this.uuid
  }
}
