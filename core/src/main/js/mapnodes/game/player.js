/*
 * Copyright 2020 Year4000. All Rights Reserved.
 */
import _ from 'lodash'

import Facts from '../facts.js'
import Messages from '../messages.js'
import { is_true, not_null } from '../conditions.js'
import { listener } from '../events/event_manager.js'
import { inject } from '../injection.js'


/** @typedef {import('wolfy87-eventemitter')} EventEmitter */
/** @typedef {import('./game.js').default} Game */
/** @typedef {import('./team.js').default} Team */

/**
 * This will serialize the uuid to be used for the lookup table
 *
 * @param {string} uuid
 * @return {string}
 */
const serializeUuid = (uuid) => uuid.replace(/-/g, '').toLowerCase()

/**
 * Will take any assumed to be uuid and make sure the hyphens are in there
 *
 * @param {string} uuid
 * @return {string}
 */
const hyphenedUuid = (uuid) => serializeUuid(uuid).replace(/(\w{8})(\w{4})(\w{4})(\w{4})(\w{12})/, '$1-$2-$3-$4-$5')

/**
 * The instance of all the player that has ever joined this server's instance
 *
 * @type {{ [key: string]: Player }}
 */
const _player_instances = {}

/** Generates the player object */
export default class Player {
  /** @type {EventEmitter} */
  @inject() event_manager
  /** @type {Game} */
  @inject() game

  /**
   * @param {string} username
   * @param {string} uuid
   */
  constructor(username, uuid) {
    not_null(username, 'username')
    not_null(uuid, 'uuid')
    this._meta = { username, uuid: hyphenedUuid(uuid), id: serializeUuid(uuid) }
    this._current_team = null
    _player_instances[this._meta.id] = this
    _player_instances[username.toLowerCase()] = this // todo there may be a name clash, should add some checks later
  }

  /**
   * Create the instance of this player
   *
   * @param {string} player
   * @return {Player}
   */
  static of(player) {
    not_null(player, 'player')
    is_true(typeof player === 'string')
    // here we will serialize the name since usernames can not have - in it
    const serializePlayer = serializeUuid(player)
    if (serializePlayer in _player_instances) { // check the cache first
      return _player_instances[serializePlayer]
    }
    // Cache player does not exist get from the Minecraft server
    if (_.size(player) > Facts.MAX_USERNAME_SIZE) { // must be uuid
      return Player.of_uuid(hyphenedUuid(player))
    } // Must be username
    return Player.of_username(player)
  }

  /**
   * Generate the player meta from the uuid
   *
   * @param {string} uuid
   * @return {Player}
   */
  static of_uuid(uuid) {
    not_null(uuid, 'uuid')
    return new Player(...$.bindings.player_meta_uuid(String(uuid)).split(':'))
  }

  /**
   * Generate the player meta from a username
   *
   * @param {string} username
   * @return {Player}
   */
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
    $.bindings.send_message(this.uuid, `${message}`)
  }

  /**
   * Check if the player has the permission
   *
   * @param {string} permission
   * @return {boolean}
   */
  has_permission(permission) {
    return $.bindings.has_permission(this.uuid, `${permission}`)
  }

  /** Set the header for the tablist */
  set tablist_header(header) {
    $.bindings.tablist_header(this.uuid, `${header}`)
  }

  /** Set the header for the tablist */
  set tablist_footer(footer) {
    $.bindings.tablist_footer(this.uuid, `${footer}`)
  }

  teleport(x, y, z, yaw, pitch) {
    $.bindings.teleport(this.uuid, x, y, z, yaw ?? 0, pitch ?? 0)
  }

  /** Start the game for the player */
  start() {
    this.event_manager.trigger('start_player', [this, this.game])
  }

  /** Stop the game for the player */
  stop() {
    this.event_manager.trigger('stop_player', [this, this.game])
    this.leave_team()
  }

  /** Check if the player is playing */
  is_playing() {
    return this._current_team && this._current_team.id !== Facts.SPECTATOR_ID && this.game.is_running()
  }

  /** Check if the player is playing */
  is_spectating() {
    return this._current_team && this._current_team.id === Facts.SPECTATOR_ID
  }

  // THIS IS A PROTOTYPE OF HOW THIS WOULD WORK, this does not run currently
  @listener('player_join_team')
  on_player_join_team({ team }) {
    this._current_team = team
  }

  /**
   * Have the player join the specific team
   *
   * @param {Team} team
   */
  join_team(team) {
    not_null(team, 'team')
    if (this.game.is_running() && this.is_playing()) {
      Messages.TEAM_MENU_NOT_NOW.send(this)
      return
    }
    if (this.game.is_running() && this.is_spectating()) {
      this.leave_team()
      team.join(this)
      this._current_team = team
      this.start()
      return
    }
    team.join(this)
    this._current_team = team
  }

  /** Have the player leave the game they are on */
  leave_team() {
    if (this._current_team) {
      this._current_team.leave(this)
      this._current_team = null
    }
  }

  /**
   * Get the current team for the player, this should always return something
   *
   * @return {Team}
   */
  get team() {
    return this._current_team
  }

  /** Checks if the two player objects are equal */
  is_equal(player) {
    // only use == since the strings are not exactly the same string
    // eslint-disable-next-line eqeqeq
    return typeof player === 'object' && this.valueOf() == player.valueOf()
  }

  /** Get the value of this player */
  valueOf() {
    return this._meta.id
  }
}

/** Export a dummy player that would be Steve */
export const STEVE_UUID = 'fffffff0-ffff-fff0-ffff-fff0fffffff0'
export const STEVE = new Player('Steve', STEVE_UUID)

/** Export a dummy player that would be Alex */
export const ALEX_UUID = 'fffffff0-ffff-fff0-ffff-fff0fffffff1'
export const ALEX = new Player('Alex', ALEX_UUID)
