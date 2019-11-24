import assert from 'assert'
import Player, { ALEX, ALEX_UUID, STEVE, STEVE_UUID } from './player.js'

describe('player', () => {
  it('same player', () => assert.equal(ALEX, ALEX))
  it('not same player', () => assert.notEqual(ALEX, STEVE))
  it('same player look up with uuid', () => assert.equal(Player.of(ALEX_UUID), ALEX))
  it('same player look up with username', () => assert.equal(Player.of('Steve').uuid, STEVE_UUID))
  it('same player look up with serialized uuid', () => assert.equal(Player.of(ALEX_UUID.replace('-', '')), ALEX))
  it('serialized uuid matches normal uuid', () => assert.equal(Player.of(ALEX_UUID.replace('-', '')).uuid, ALEX_UUID))
})
