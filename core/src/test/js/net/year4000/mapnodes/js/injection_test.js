/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
'use strict'

const mocha = require('mocha')
require('./conditions')
require('./injection')

mocha.describe('Injection', function() {
  mocha.it('Test Init', function() {
    console.log(new Injector({}))
  })
})
