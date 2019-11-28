import { game_mode } from '../game_registry.js'
import AbstractGameMode from '../abstract_game_mode.js'

/**
 * Spleef is a game mode where players have a wand that shoots snowballs that break blocks.
 * All while players must constancy run around as the floor will decay in stages.
 */
@game_mode('spleef')
export default class Spleef extends AbstractGameMode {
}
