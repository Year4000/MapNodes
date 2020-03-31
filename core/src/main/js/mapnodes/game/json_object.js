/*
 * Copyright 2020 Year4000. All Rights Reserved.
 */
import _ from 'lodash'

import { is_object, not_null } from '../conditions.js'


/** Represents a team from the json object */
export default class JsonObject {
  /**
   * Make sure the JsonObject has the id and the object
   *
   * @param {string} id
   * @param {object} json
   */
  constructor(id, json) {
    this._id = not_null(id, 'id')
    this._json = not_null(json, 'json')
  }

  /**
   * Get the id of this JsonObject, also makes the id lowercase
   *
   * @return {string}
   */
  get id() {
    return _.lowerCase(this._id)
  }

  /**
   * Get the name for the object defaults to id
   *
   * @return {string}
   */
  get name() {
    return this._id
  }

  /**
   * Will verify that the JSON object matches the provided schema.
   *
   * This will take the Object from the static schema property can run checks on
   * it. In this case its better to use the get keyword to avoid evaluation of the
   * Object before its needed.
   *
   * Example:
   *
   * static get schema() {
   *   return {
   *     key: { type: boolean, value: false },
   *     foo: { type: String, value: 'bar' }
   *   }
   * }
   *
   * @param {object} json object the json to validate the schema on, defaults to the internal json
   * @param {object} schema defaults to the static schema property
   * @return {boolean}
   */
  verify(json = this._json, schema = this.constructor.schema) {
    is_object(not_null(json, 'Json must exist'), 'Must be a JSON object')
    not_null(schema, 'Each object must have a schema associated with it')
    // todo verify that the json matches the given schema more verbose checking
    return _.keys(json).reduce((and, key) => {
      const value = json[key]
      const { type } = schema[key]
      const types = Array.isArray(type) ? type : [type]
      return and && types.reduce((or, schemaType) => (
        // eslint-disable-next-line valid-typeof
        or || (schemaType === 'array' ? Array.isArray(value) : (schemaType !== undefined && typeof value === schemaType))
      ), false)
    }, true)
  }

  /**
   * The JSON object of this object
   *
   * @return {object}
   */
  toJSON() {
    return this._json
  }
}
