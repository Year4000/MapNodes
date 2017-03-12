/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */

/** Represents a team from the json object */
class Team extends JsonObject {

  constructor(id, team) {
    super(id, team)
    this._members = []
  }

  /** Get the json for this team */
  get team() {
    return super._json
  }

  /** Have the player join this team */
  join(player) {
    Conditions.not_null(player, 'player')
    this._members.push(player)
    this.$event_emitter.trigger('join_game', [player, this])
  }

  /** Get the size of the team */
  get size() {
    return _.size(this._members)
  }
}
