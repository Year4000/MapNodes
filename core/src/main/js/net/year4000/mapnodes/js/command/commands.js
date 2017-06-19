/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
'use strict'

/** Simple ping / pong command to test command system */
map_nodes.register_command('ping', (executor, args) => {
  executor.send_message('&6pong!')
})

/** Have the executor join the smallest team */
map_nodes.register_command('team', (executor, args) => {
  if (executor.is_player()) {
    let team = map_nodes.current_game._smallest_team;
    executor.send_message(`&6Joining team&8: ${team.color_name}`)
    executor.player.join_team(team)
  }
})
