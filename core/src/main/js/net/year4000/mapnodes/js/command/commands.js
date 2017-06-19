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
    Messages.TEAM_JOIN.send(executor.player, [team.color_name])
    executor.player.join_team(team)
  }
})

/** Just check the locale of the current user */
map_nodes.register_command('locale', (executor, args) => {
  Messages.CMD_MAPNODES_LOCALE_NAME.send(executor.player, [Messages.LOCALE_NAME.get(executor.player)])
  Messages.CMD_MAPNODES_LOCALE_CODE.send(executor.player, [Messages.LOCALE_CODE.get(executor.player)])
})