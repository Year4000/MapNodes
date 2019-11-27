/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
import _ from 'lodash'
import { REVISION } from 'three'
import Logger from 'js-logger'
import Injector, { inject } from './injection.js'
import { not_null } from './conditions.js'
import CommandManager from './command/cmd_manager.js'
import { event_manager, listener } from './events/event_manager.js'
import Messages from './messages.js'

/** The service to handle pretty much everything with the JS side of MapNodes */
class MapNodes {

  @inject(Injector) $injector
  @inject(CommandManager) $command_manager

  constructor() {
    // Create the injector and inject our self with the injector
    let self = this
    this.$injector = new Injector({
      map_nodes: self,
      command_manager: new CommandManager(),
    })
  }

  /** Get the command manager for mapnodes */
  register_command(command, action) {
    return this.$command_manager.register_command(command, action)
  }

  /** Get the current game */
  get current_game() {
    return not_null(this._current_game, '_current_game')
  }

  /** Set the current game and set the current game to the last game */
  set current_game(game) {
    not_null(game, 'game')
    this._last_game = this._current_game
    this._current_game = game
    this._current_game.$injector = this.$injector.child_injector({
      game: this._current_game,
    })
    this._current_game.register_map()
    if (this._last_game) { // Cycle to the next game
      this._last_game.cycle(game)
    }
  }

  // Event Listeners

  /** Called when the system has been loaded  */
  @listener('load')
  static on_load() {
    Logger.setHandler((messages, context) => println(`[${context.level.name}] ${messages[0]}`))
    Logger.setLevel($.bindings.debug() ? Logger.DEBUG : Logger.INFO)
    Logger.info('Loading environment from the Javascript side')
    Logger.info(`V8 Engine Version: ${$.bindings.v8_version()}`)
    Logger.info(`Lodash Version: ${_.VERSION}`)
    Logger.info(`Three Version: ${REVISION}`)
    Logger.info(`Logger Version: ${Logger.VERSION}`)
  }

  /** Let us know that a player joined the team */
  @listener('join_team')
  static on_join_team({username}, {name, size}, game) {
    Logger.info(`The player ${username} joined the team ${name} size ${size}`)
  }

  @listener('join_game')
  static on_join_game(player) {
    MapNodes._tab_list(player)
    player.tablist_footer = player.$game.settings.tab_list_footer
    // todo open team selector gui
  }

  @listener('start_player')
  static on_start_player(player) {
    MapNodes._tab_list(player)
    // todo set kit
    // todo set scoreboard
    if (player.is_playing()) { // Message when the game starts for players
      let {name, version, description, authors} = player.$game.map.map
      let author_names = _.map(authors, author => _.truncate(author, {length: 3}));
      player.send_message()
      player.send_message('&7&m' + _.pad(`&a ${name} &7${version.replace(/\./, '&8.&7')} &7&m`, 55, '*'))
      player.send_message(Messages.MAP_CREATED.get(player) + author_names)
      player.send_message('&a&o' + description) // todo multi line description
      player.send_message('&7&m' + '*'.repeat(45))
      player.send_message()
      // send player to spawn
      player.teleport(...player.team.spawn_point.toArray())
    }
  }

  @listener('stop_game_player')
  static on_stop_game_player(player) {
    MapNodes._tab_list(player)
  }

  static _tab_list(player) {
    event_manager.trigger('tablist_header', [player, player.$game])
    player.tablist_header = player.$game.tablist_header
  }
}

export const map_nodes = new MapNodes()
