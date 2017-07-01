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
      event_manager: new EventManager(),
    })
    this.register_listeners(MapNodes)
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

  /** Register all listeners into the event emitter */
  register_listeners(clazz) {
    Reflect.ownKeys(clazz).filter(name => _.endsWith(name, '$listener')).forEach(name => {
      let event_name = name.substring(0, name.indexOf('$'))
      this.$event_emitter.on(event_name, Reflect.get(clazz, name))
    })
  }

  // Event Listeners

  /** Called when the system has been loaded  */
  static load$listener() {
    Logger.setHandler((messages, context) => println(`[${context.level.name}] ${messages[0]}`))
    Logger.setLevel($.bindings.debug() ? Logger.DEBUG : Logger.INFO)
    Logger.info('Loading environment from the Javascript side')
    Logger.info('V8 Engine Version: ' + $.bindings.v8_version())
    Logger.info('Lodash Version: ' + _.VERSION)
    Logger.info('Three Version: ' + THREE.REVISION)
    Logger.info('Logger Version: ' + Logger.VERSION)
  }

  /** Let us know that a player joined the team */
  static join_team$listener(player, team, game) {
    Logger.info(`The player ${player.username} joined the team ${team.name} size ${team.size}`)
  }

  static join_game$listener(player) {
    MapNodes._tab_list(player)
    // todo open team selector gui
  }

  static start_player$listener(player) {
    MapNodes._tab_list(player)
    // todo set kit
    // todo set scoreboard
    if (player.is_playing()) { // Message when the game starts for players
      let map_info = player.$game.map.map
      player.send_message()
      player.send_message('&7&m' + _.pad(`&a ${map_info.name} &7${map_info.version.replace(/\./, '&8.&7')} &7&m`, 55, '*'))
      player.send_message(Messages.MAP_CREATED.get(player) + map_info.authors)
      player.send_message('&a&o' + map_info.description) // todo multi line description
      player.send_message('&7&m' + _.repeat('*', 45))
      player.send_message()
    }
  }

  static stop_game_player$listener(player) {
    MapNodes._tab_list(player)
  }

  static _tab_list(player) {
    map_nodes.$event_emitter.trigger('tablist_header', [player, player.$game])
    player.tablist_header = player.$game.tablist_header
  }
}

const map_nodes = new MapNodes()
