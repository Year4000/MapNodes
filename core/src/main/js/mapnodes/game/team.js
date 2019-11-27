/*
 * Copyright 2019 Year4000. All Rights Reserved.
 */
import _ from 'lodash'
import { event_manager } from '../events/event_manager.js'
import JsonObject from './json_object.js'
import { not_null } from '../conditions.js'
import Colors from '../colors.js'
import Regions from '../regions/regions.js'
import { listener } from "../events/event_manager.js"

/** Represents a team from the json object */
export default class Team extends JsonObject {

  _members = []

  /** This follows the documented scheme here https://resources.year4000.net/mapnodes/teams_component */
  static get schema() {
    return {
      name: { type: 'string' },
      color: { type: 'string', value: 'white' },
      kit: { type: 'string' },
      size: { type: 'number', value: 1 },
      spawns: { type: 'array', value: [] },
      friendly_fire: { type: 'boolean', value: false },
      friendly_invisibles: { type: 'boolean', value: true },
    }
  }

  /** Get the json for this team */
  get team() {
    return this._json
  }

  /** The name of the team, use legacy first then default to new standard */
  get name() {
    return this.team.name || super.name()
  }

  /** Get the name of the team with the color code */
  get color_name() {
    return this.color_code + this.name
  }

  /** Get the name of the color the team bellongs to */
  get color() {
    return _.lowerCase(this.team.color)
  }

  /** Get the color code for the team */
  get color_code() {
    return Colors[this.color] || '&f&k'
  }

  // THIS IS A PROTOTYPE OF HOW THIS WOULD WORK, this does not run currently
  @listener('team_join_player')
  on_team_join_player({ player }) {
    this._members.push(player)
  }

  /** Have the player join this team */
  join(player) {
    not_null(player, 'player')
    if (player._current_team) { // Swap the teams the player is on
      event_manager.trigger('swap_team', [player, player._current_team, this, this.$game])
      player.leave_team()
    }
    this._members.push(player)
    event_manager.trigger('join_team', [player, this, this.$game])
  }

  /** Tell the player its time to start */
  start_player(player) {
    event_manager.trigger('start_team_player', [player, this, this.$game])
    player.start()
  }

  /** Have the entire team start */
  start() {
    event_manager.trigger('start_team', [this, this.$game])
    _.forEach(this._members, member => this.start_player(member))
  }

  /** Have the player leave the team*/
  leave(player) {
    not_null(player, 'player')
    _.remove(this._members, object => object.is_equal(player))
    event_manager.trigger('leave_team', [player, this, this.$game])
  }

  /** Get the size of the team */
  get size() {
    return _.size(this._members)
  }

  /** Lazy load all spawn regions */
  get spawns() {
    return this._spawns || (this._spawns = _.map(this.team.spawns, zone => Regions.map_region(zone)))
  }

  /** Get a random point from the list of spawns */
  get spawn_point() {
    // todo random spawn
    return this.spawns[0].points.first()
  }
}
