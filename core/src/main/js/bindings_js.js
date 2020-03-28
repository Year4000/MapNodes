/*
 * Copyright 2020 Year4000. All Rights Reserved.
 */
import Logger from 'js-logger'
import { not_null } from './mapnodes/conditions.js'
import { map_nodes } from './mapnodes/mapnodes.js'
import CommandExecutor from './mapnodes/command/cmd_executor.js'
import Game from './mapnodes/game/game.js'
import Player from './mapnodes/game/player.js'


/** This map stores the function bindings that are in javascript */
const JS = {}

/** Will register the method in the bindings map for $.js */
const bind = (alias) => {
  const to_lower_camel = (string) => {
    not_null(string, 'A string value must exist')
    let out = ''
    let upper = false
    for (const c of string) {
      if (upper) { // uppercase the letter and rest it flag
        out += c.toUpperCase()
        upper = false
      } else if (c === '_') { // dont add _ and trigger next letter must be upper
        upper = true
      } else { // append the char
        out += c
      }
    }
    return out
  }
  return (handler, key) => {
    // Map the functions to the java names
    if (!(alias in JS)) {
      JS[alias ?? to_lower_camel(key)] = handler
    }
    return handler
  }
}

/**
  Bindings is the bridge between the multiple languages. The bindings allows
  for object programing, but still allow the speed that is needed. Since only
  the low level forms the data types are transmitted between.
*/

/** Is the game currently running */
export const is_game_running = bind('isGameRunning')(() => { // eslint-disable-line arrow-body-style
  return map_nodes.current_game.is_running()
})

/** Load the environment */
export const load = bind('load')(() => {
  map_nodes.event_manager.trigger('load')
})

/** View only access to the event in minecraft */
export const on_event = bind('onEvent')((event_id, event) => {
  map_nodes.event_manager.trigger(event_id, [event])
})

/** Swap out the current game */
export const swap_game = bind('swapGame')((id, map) => {
  map_nodes.current_game = new Game(id, map)
})

/** Start the current game */
export const start = bind('start')(() => {
  map_nodes.current_game.start()
})

/** Stop the current game */
export const stop = bind('stop')(() => {
  map_nodes.current_game.stop()
})

/** Get the current state of the game */
export const game_state = bind('gameState')(() => { // eslint-disable-line arrow-body-style
  return map_nodes.current_game._state
})

/** Join the player to the game */
export const join_game = bind('joinGame')((player) => {
  map_nodes.current_game.join_game(player)
})

/** Leave the player to the game */
export const leave_game = bind('leaveGame')((player) => {
  map_nodes.current_game.leave_game(player)
})

/** Pass the command to the manager */
export const on_player_command = bind('onPlayerCommand')((uuid, username, command, args) => {
  const player = new CommandExecutor(Player.of(uuid))
  return map_nodes.command_manager.execute_command(player, command, args)
})

/** Check if the command part of the system */
export const is_command = bind('isCommand')((command) => {
  if (!command) {
    return false
  }
  return map_nodes.command_manager.is_command(command)
})

/** Send the x,y,z cords of the spawn point */
export const spawn_point = bind('spawnPoint')(() => {
  const point = map_nodes.current_game.spawn_point
  if (point) {
    return [point.x, point.y, point.z]
  }
  Logger.warn('There was no spawn point found, using [0, 64, 0]')
  return [0, 64, 0]
})

// Use the default export as the new mapped bindings
export default JS
