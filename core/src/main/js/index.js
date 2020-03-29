/*
 * Copyright 2020 Year4000. All Rights Reserved.
 */
import './polyfills.js'
import './bindings_js.js'
import * as globals from './globals.js'
import './mapnodes/command/commands.js'


// Inject our globals into the global object
Object.entries(globals).forEach(([key, value]) => {
  try {
    global[key] = value
  } catch (error) {
    console.error(error)
  }
})
