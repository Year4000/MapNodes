/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */
import _ from 'lodash'
import { not_null } from '../conditions.js'

/** Represents a team from the json object */
export default class JsonObject {

  /** Make sure the JsonObject has the id and the object */
  constructor(id, json) {
    this._id = not_null(id, 'id')
    this._json = not_null(json, 'json')
  }

  /** Get the id of this JsonObject, also makes the id lowercase */
  get id() {
    return _.lowerCase(this._id)
  }

  /** Get the name for the object defaults to id */
  get name() {
    return this._id
  }

  /** The JSON object of this object */
  toJSON() {
    return this._json
  }
}
