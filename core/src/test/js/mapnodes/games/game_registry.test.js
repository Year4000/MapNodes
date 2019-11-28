import assert from 'assert'
import { game_registry } from './game_registry.js'

// This triggers the dynamic registration of the game mode
import './skywars/skywars.js'
import './destroy/destroy.js'

describe('Game Registry', () => {
  it('skywars game mode registered', () => assert.ok(game_registry.is_registered('skywars')))
  it('destroy game mode registered', () => assert.ok(game_registry.is_registered('destroy')))
  it('game modes count', () => assert.equal(game_registry.registered_ids.length, 2))
})
