/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */

/** Represents a team from the json object */
class Team extends JsonObject {

  constructor(id, team) {
    super(id, team);
  }

  /** Get the json for this team */
  get team() {
    return super._json;
  }
}
