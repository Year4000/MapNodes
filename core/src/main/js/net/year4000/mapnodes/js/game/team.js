/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */

/** Represents a team from the json object */
class Team extends JsonObject {

  constructor(id, team) {
    super(id, team)
    this._members = []
    this._spawns = _.map(team.spawns, zone => Regions.map_region(zone))
  }

  /** Get the json for this team */
  get team() {
    return this._json
  }

  /** The name of the team */
  get name() {
    return this.team.name
  }

  /** Have the player join this team */
  join(player) {
    Conditions.not_null(player, 'player')
    if (player._current_team) { // Swap the teams the player is on
      player.leave()
      this.$event_emitter.trigger('swap_team', [player, player._current_team, this, this.$game])
    }
    this._members.push(player)
    player._team = this
    this.$event_emitter.trigger('join_team', [player, this, this.$game])
  }

  /** Tell the player its time to start */
  start_player(player) {
    this.$event_emitter.trigger('start_team', [player, this, this.$game])
  }

  /** Have the entire team start */
  start() {
    _.forEach(this._members, member => this.start_player(member))
  }

  /** Have the player leave the team*/
  leave(player) {
    Conditions.not_null(player, 'player')
    _.remove(this._members, object => object.is_equal(player))
    this.$event_emitter.trigger('leave_team', [player, this, this.$game])
  }

  /** Get the size of the team */
  get size() {
    return _.size(this._members)
  }

  /** Get a random point from the list of spawns */
  get spawn_point() {
    // todo random spawn
    return this._spawns[0].points.first()
  }
}
