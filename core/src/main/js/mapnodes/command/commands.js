/*
 * Copyright 2020 Year4000. All Rights Reserved.
 */
import Facts from '../facts.js'
import Messages from '../messages.js'
import { map_nodes } from '../mapnodes.js'


/** Simple ping / pong command to test command system */
map_nodes.register_command('ping', (executor) => {
  executor.send_message('&6pong!')
})

/** Have the executor join the smallest team */
map_nodes.register_command('team', (executor, args) => {
  if (executor.is_player()) {
    const team = (args === '') ? map_nodes.current_game._smallest_team : map_nodes.current_game._teams.get(args)
    if (team) {
      Messages.TEAM_JOIN.send(executor.player, [team.color_name])
      executor.player.join_team(team)
    } else {
      Messages.TEAM_NOT_FOUND.send(executor.player, [args])
    }
  }
})

/** Have the executor join the smallest team */
map_nodes.register_command('spec', (executor) => {
  if (executor.is_player()) {
    const team = map_nodes.current_game._teams.get(Facts.SPECTATOR_ID)
    Messages.TEAM_JOIN.send(executor.player, [team.color_name])
    executor.player.leave_team()
    executor.player.join_team(team)
    executor.player.teleport(...team.spawn_point.toArray())
  }
})

/** Just check the locale of the current user */
map_nodes.register_command('locale', (executor) => {
  Messages.CMD_MAPNODES_LOCALE_NAME.send(executor.player, [Messages.LOCALE_NAME.get(executor.player)])
  Messages.CMD_MAPNODES_LOCALE_CODE.send(executor.player, [Messages.LOCALE_CODE.get(executor.player)])
})
