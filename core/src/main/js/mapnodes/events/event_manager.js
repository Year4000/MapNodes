/*
 * Copyright 2020 Year4000. All Rights Reserved.
 */
import EventEmitter from 'wolfy87-eventemitter'

/*
 * This event system is directly tied to the object models in a given class,
 * Each class type is registered with the EventManager and when that type of event if emitted
 * it is then passed down to the correct instance.
 *
 * This may also be tied directly with the injection system
 *
 * Handled in a class instance
 *
 * class Player {
 *
 *   @listener('player_join')
 *   on_player_join(event) {
 *    this.send_message(`${this.username} has joined the game.`)
 *   }
 *
 *   // Another event from a different binding
 *   @listener('game_start')
 *   static on_game_start(game, event) {}
 * }
 *
 * Handled in the map json
 *
 * {
 *   events: {
 *     on_player_join: (player, event) => player.send_message(`${player.username} has joined the game.`)
 *   }
 * }
 *
 * Unhandled Event Type or Unbound Event
 *
 * @listener('player_join')
 * function on_player_join(player, event) {
 *   this.send_message(`${this.username} has joined the game.`)
 * }
 *
 *
 * This is a global system that is for MapNodes so there will exist a static content on registered types.
 * Constructed instances are registered into the system and deconstructed objects are then unregistered.
 *
 * Example of event types
 *
 * player_join
 * player_leave
 * player_join_team => team_join_player     # Maybe have it be a dual event that also triggers team binding
 * player_leave_team => team_leave_player
 * player_break_block
 * player_place_block
 * player_attack_player
 * player_attack_mob
 *
 * game_start
 * game_end
 * game_load
 * game_unload
 */

/** Use the event emitter as the underlying event manager */
export const event_manager = new EventEmitter()

/** Prototype functionally, must be static functions right now */
export const listener = (id) => (target, key) => {
  event_manager.on(id, target[key])
  return target
}
