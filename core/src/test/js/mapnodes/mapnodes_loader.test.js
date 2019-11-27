import fs from 'fs'
import path from 'path'
import assert from 'assert'

import Map from './game/map.js'
import World from './game/world.js'

/*
  This unit test will load the map config files into the system and verify that they load properly.
 */

// Read each map and pass the map object to the callback
function readMaps(callback) {
  const TEST_MAPS_DIR = path.resolve('./build/test/js/maps/')
  fs.readdir(TEST_MAPS_DIR, (error, files) => {
    files.forEach(file => {
      const abs_path = path.resolve(TEST_MAPS_DIR, file)
      const map_file = fs.readFileSync(abs_path)
      const map = eval(`(${String(map_file)})`)
      callback({map, file, abs_path})
    })
  })
}

describe('map loader', () => {
  readMaps(({map, file}) => {
    describe(`testing ${file}`, () => {
      // Map Component
      const map_component = new Map(map.map || {})
      it(`map component ${map_component.valueOf()}`, () => assert.ok(map_component.verify()))
      // World Component
      it('world component', () => assert.ok(new World(map.world || {}).verify()))
    })
  })
})
