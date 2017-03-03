/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */

/** Represents a team from the json object */
class JsonObject {

  /** Make sure the JsonObject has the id and the object */
  constructor(id, json) {
    this._id = Conditions.not_null(id, 'id');
    this._json = Conditions.not_null(json, 'json');
  }

  /** Get the id of this JsonObject */
  get id() {
    return this._id;
  }

  /** The JSON object of this object */
  toJSON() {
    return this._json;
  }
}
