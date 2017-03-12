/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */

/**
  Bindings is the bridge between the multiple languages. The bindings allows
  for object programing, but still allow the speed that is needed. Since only
  the low level forms the data types are transmitted between.
*/

// noinspection ES6ConvertVarToLetConst
/** This constant is created by the runtime no need for it */
var PLATFORM = PLATFORM || 'none'

// noinspection ES6ConvertVarToLetConst
/** This constant is created by the runtime no need for it */
var JAVA = JAVA || {}

/** The constants that are known when the JS runtime is created */
const PLATFORMS = {
  "PC": "java", // Sponge
  "PE": "java" // Nukkit
}

/** Used to interact the Javascript with MapNodes base game */
class $ {
  /** Get the instance of this javascript object */
  static get js() {
    return $._js || ($._js = new $())
  }

  /** Map a specific object to this bindings var */
  static get bindings() {
    if (PLATFORM == PLATFORMS.PE) {
      return JAVA
    } else if (PLATFORM == PLATFORMS.PC) {
      return JAVA
    } else {
      throw "PLATFORM is not defined!"
    }
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
}

/** Include the resource into the V8 runtime */
function include(path) {
  $.bindings._include(path)
}

/** Print the message to the console */
function print(message) {
  $.bindings.print(message)
}

/** Print a line to the console */
function println(message) {
  print(message + "\n")
}

/** Dump the var to the screen */
function var_dump(variable) {
  println(JSON.stringify(variable))
}
