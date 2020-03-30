import assert from 'assert'

import { game_registry } from './game_registry.js' // eslint-disable-line import/no-unresolved

// This triggers the dynamic registration of the game mode
import './skywars/skywars.js' // eslint-disable-line import/no-unresolved
import './destroy/destroy.js' // eslint-disable-line import/no-unresolved


describe('Game Registry', () => {
  it('skywars game mode registered', () => assert.ok(game_registry.is_registered('skywars')))
  it('destroy game mode registered', () => assert.ok(game_registry.is_registered('destroy')))
  it('game modes count', () => assert.equal(game_registry.registered_ids.length, 2))
})
