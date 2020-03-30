import fs from 'fs'
import path from 'path'
import assert from 'assert'

import _ from 'lodash'

import Map from './game/map.js' // eslint-disable-line import/no-unresolved
import World from './game/world.js' // eslint-disable-line import/no-unresolved
import Team from './game/team.js' // eslint-disable-line import/no-unresolved
import Region from './game/region.js' // eslint-disable-line import/no-unresolved
import Kit from './game/kit.js' // eslint-disable-line import/no-unresolved


/*
  This unit test will load the map config files into the system and verify that they load properly.
  This unit test only tests if the objects will match the schema built in the system. This test does not
  cover nested object right now.
 */

// Read each map and pass the map object to the callback
function readMaps(callback) {
  const TEST_MAPS_DIR = path.resolve('./build/test/js/maps/')
  fs.readdir(TEST_MAPS_DIR, (error, files) => {
    files.forEach((file) => {
      const abs_path = path.resolve(TEST_MAPS_DIR, file)
      const map_file = fs.readFileSync(abs_path)
      const map = eval(`(${String(map_file)})`)
      callback({ map, file, abs_path })
    })
  })
}

describe('map loader', () => {
  readMaps(({ map, file }) => {
    describe(`testing ${file}`, () => {
      // Map Component
      const map_component = new Map(map.map ?? {})
      it(`map component ${map_component.valueOf()}`, () => assert.ok(map_component.verify()))
      // World Component
      it('world component', () => assert.ok(new World(map.world ?? {}).verify()))
      // Teams Component
      _.forEach(map.teams, (json, id) => {
        it(`team component ${id}`, () => assert.ok(new Team(id, json).verify()))
      })
      // Regions Component
      _.forEach(map.regions, (json, id) => {
        it(`region component ${id}`, () => assert.ok(new Region(id, json).verify()))
      })
      // Kits Component
      _.forEach(map.kits, (json, id) => {
        it(`kit component ${id}`, () => assert.ok(new Kit(id, json).verify()))
      })
    })
  })
})
