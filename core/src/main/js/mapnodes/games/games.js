import { game_registry } from './game_registry.js'
import Capture from './capture/capture.js'
import Deathmatch from './deathmatch/deathmatch.js'
import Destroy from './destroy/destroy.js'
import SkyWars from './skywars/skywars.js'
import Spleef from './spleef/spleef.js'
import TntWars from './tnt_wars/tnt_wars.js'

/** Export all enabled game modes, as the import of the game modes will registered them into the registry */
export {
  game_registry,
  Capture,
  Deathmatch,
  Destroy,
  SkyWars,
  Spleef,
  TntWars,
}
