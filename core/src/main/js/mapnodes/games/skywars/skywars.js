import { game_mode } from '../game_registry.js'
import AbstractGameMode from '../abstract_game_mode.js'

/**
 * SkyWars is a game mode that is a FFA where players fight until the last one stands.
 */
@game_mode('skywars')
export default class SkyWars extends AbstractGameMode {
}
