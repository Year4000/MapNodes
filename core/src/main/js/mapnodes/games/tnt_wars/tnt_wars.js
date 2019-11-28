import { game_mode } from '../game_registry.js'
import AbstractGameMode from '../abstract_game_mode.js'

/**
 * TNT Wars is a game mode where player must build tnt cannons and become the first team
 * to destroy the other teams island.
 */
@game_mode('tnt_wars')
export default class TntWars extends AbstractGameMode {
}
