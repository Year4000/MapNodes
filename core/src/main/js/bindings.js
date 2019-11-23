/*
 * Copyright 2019 Year4000. All Rights Reserved.
 */
import Logger from 'js-logger'
import { map_nodes } from './mapnodes/mapnodes.js'
import Game from './mapnodes/game/game.js'
import Player from './mapnodes/game/player.js'
import CommandExecutor from './mapnodes/command/cmd_executor.js'

/**
  Bindings is the bridge between the multiple languages. The bindings allows
  for object programing, but still allow the speed that is needed. Since only
  the low level forms the data types are transmitted between.
*/

// noinspection ES6ConvertVarToLetConst
/** This constant is created by the runtime no need for it */
var PLATFORM = global.PLATFORM || 'none'

// noinspection ES6ConvertVarToLetConst
/** This constant is created by the runtime no need for it */
var JAVA = global.JAVA || {}

/** The constants that are known when the JS runtime is created */
const PLATFORMS = {
  "PC": "java", // Sponge
  "PE": "java" // Nukkit
}

/** Used to interact the Javascript with MapNodes base game */
global.$ = class $ {
  /** Get the instance of this javascript object */
  static get js() {
    return $._js || ($._js = new $())
  }

  /** Map a specific object to this bindings var */
  static get _bindings() {
    if (PLATFORM === PLATFORMS.PE) {
      return JAVA
    } else if (PLATFORM === PLATFORMS.PC) {
      return JAVA
    } else {
      throw "PLATFORM is not defined!"
    }
  }

  /** Wrap the internal bindings in a proxy to catch unimplemented variables */
  static get bindings() {
    if ($._proxy == null) { // Lazy load the proxy
      $._proxy = new Proxy($._bindings, {
        get: (target, name, receiver) => {
          if (target[name]) {
            return function() { // Must be a function to capture ...arguments
              try {
                return target[name](...arguments)
              } catch (any) {
                Logger.error(`An error has been thrown from: ${PLATFORM}`)
                Logger.error(any)
                return () => {}
              }
            }
          } else {
            Logger.error(`${name} has not been defined in the bindings mappings`)
            return () => {}
          }
        }
      })
    }
    return $._proxy
  }

  /** This function just makes sure the bindings were properly established */
  platform_name() { // @Bind
    return PLATFORM
  }

  /** Is the game currently running */
  is_game_running() { // @Bind
    return map_nodes.current_game.is_running()
  }

  /** Load the environment */
  load() { // @Bind
    map_nodes.$event_emitter.trigger('load')
  }

  /** View only access to the event in minecraft */
  on_event(event_id, event) { // @Bind
    map_nodes.$event_emitter.trigger(event_id, [event])
  }

  /** Swap out the current game */
  swap_game(id, map) {
    map_nodes.current_game = new Game(id, map)
  }

  /** Start the current game */
  start() {
    map_nodes.current_game.start()
  }

  /** Stop the current game */
  stop() {
    map_nodes.current_game.stop()
  }

  /** Get the current state of the game */
  game_state() {
    return map_nodes.current_game._state
  }

  /** Join the player to the game */
  join_game(player) {
    map_nodes.current_game.join_game(player)
  }

  /** Leave the player to the game */
  leave_game(player) {
    map_nodes.current_game.leave_game(player)
  }

  /** Pass the command to the manager */
  on_player_command(uuid, username, command, args) {
    let player = new CommandExecutor(Player.of(uuid))
    return map_nodes.$command_manager.execute_command(player, command, args)
  }

  /** Check if the command part of the system */
  is_command(command) {
    if (!command) {
      return false
    }
    return map_nodes.$command_manager.is_command(command)
  }

  /** Send the x,y,z cords of the spawn point */
  spawn_point() {
    let point = map_nodes.current_game.spawn_point
    if (point) {
      return [point.x, point.y, point.z]
    }
    Logger.warn('There was no spawn point found, using [0, 64, 0]')
    return [0, 64, 0]
  }
}

/** Include the resource into the V8 runtime */
global.include = function include(path) {
  $._bindings._include(path)
}

/** Print the message to the console */
global.print = function print(message) {
  $._bindings.print(message)
}

/** Print a line to the console */
global.println = function println(message) {
  print(message + "\n")
}

/** Dump the var to the screen */
global.var_dump = function var_dump(variable) {
  println(JSON.stringify(variable))
}
