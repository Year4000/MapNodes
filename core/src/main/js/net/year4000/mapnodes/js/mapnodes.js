/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
'use strict'

/** The service to handle pretty much everything with the JS side of MapNodes */
class MapNodes {

  constructor() {
    // Create the injector and inject our self with the injector
    let self = this
    this.$injector = new Injector({
      map_nodes: self,
      event_emitter: new EventEmitter(),
      command_manager: new CommandManager(),
    })
  }

  /** Get the command manager for mapnodes */
  register_command(command, action) {
    return this.$command_manager.register_command(command, action)
  }

  /** Get the current game */
  get current_game() {
    return Conditions.not_null(this._current_game, '_current_game')
  }

  /** Set the current game and set the current game to the last game */
  set current_game(game) {
    Conditions.not_null(game, 'game')
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
}

const map_nodes = new MapNodes()

// Event Listeners

/**
 The events used for the internal needs of MapNodes.
 They are able to interact with the bindings.
 Note that this file must be known after mapnodes.js.
 */

/** Called when the system has been loaded  */
map_nodes.$event_emitter.on('load', () => {
  Logger.setHandler((messages, context) => println(`[${context.level.name}] ${messages[0]}`))
  Logger.setLevel($.bindings.debug() ? Logger.DEBUG : Logger.INFO)
  Logger.info('Loading environment from the Javascript side')
  Logger.info('V8 Engine Version: ' + $.bindings.v8_version())
  Logger.info('Lodash Version: ' + _.VERSION)
  Logger.info('Three Version: ' + THREE.REVISION)
  Logger.info('Logger Version: ' + Logger.VERSION)
})

map_nodes.$event_emitter.on('join_team', (player, team, game) => {
  Logger.info(`The player ${player.username} joined the team ${team.name} size ${team.size}`)
})

/** Update the tablist when ever we need it to */
_.forEach(['join_game', 'start_player', 'stop_game_player'], key => map_nodes.$event_emitter.on(key, player => {
  map_nodes.$event_emitter.trigger('tablist_header', [player, player.$game])
  player.tablist_header = player.$game.tablist_header
}))

/** Help in debuging events */
//map_nodes.$event_emitter.on(/\\.*/, args => var_dump(args))
