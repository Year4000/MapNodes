/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
'use strict'

/** Simple ping / pong command to test command system */
map_nodes.register_command('ping', (executor, args) => {
  executor.send_message('&6pong!')
})