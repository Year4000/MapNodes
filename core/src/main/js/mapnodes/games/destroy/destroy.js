import { game_mode } from '../game_registry.js'
import AbstractGameMode from '../abstract_game_mode.js'

/**
 * Destroy is a game mode where teams must try and destroy the opponents monument.
 */
@game_mode('destroy')
export default class Destroy extends AbstractGameMode {
}
