/*
 * Copyright 2020 Year4000. All Rights Reserved.
 */
import js from './bindings_js.js'
import bindings from './bindings.js'


/** Dump the var to the screen */
export const var_dump = (variable) => console.log(JSON.stringify(variable))

/** The two way binding system */
export const $ = { js, bindings }
