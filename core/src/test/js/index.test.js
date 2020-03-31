/*
 * The unit tests for this project are slightly different as we can only test parts of the system at a time.
 * The unit tests that run only in Javascript test that the code runs the was it would without the Java handles.
 * The entire `src` folder is copied into a tmp folder `build/test/js` along with the `test` folder, then this file
 * is passed into webpack to be bundled into a single file that is then run with `mocha`.
 *
 * For the other parts that require Java, that system is handled separately with its own system via Junit.
 */

// Bellow are the unit tests the must run and pass

import './mapnodes/game/json_object.test.js'
import './mapnodes/game/player.test.js'
import './mapnodes/mapnodes_loader.test.js'
import './mapnodes/events/event_manager.test.js'
import './mapnodes/injection.test.js'
import './mapnodes/games/game_registry.test.js'
