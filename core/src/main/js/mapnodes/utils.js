/*
 * Copyright 2020 Year4000. All Rights Reserved.
 */

import { not_null } from './conditions.js'

/**
 * Create a point object with simpler syntax
 *
 * @param {number} x
 * @param {number} y
 * @param {number} z
 * @param {number} [yaw]
 * @param {number} [pitch]
 */
export const point = (x, y, z, yaw, pitch) => ({ point: { x, y, z, yaw, pitch } })

/**
 * Generate the point object and use math to generate pitch and yaw
 *
 * @param {number} x
 * @param {number} y
 * @param {number} z
 * @param {{ point: { x: number, y: number, z: number } }} point_to
 */
export const point_and_look = (x, y, z, { point: point_to }) => {
  // todo fix the trig?
  const height = Math.max(x, point_to.x) - Math.min(x, point_to.x)
  const yaw_width = Math.max(z, point_to.z) - Math.min(z, point_to.z)
  const yaw = Math.atan(yaw_width / height) * 180 / Math.PI
  const pitch_width = Math.max(y, point_to.y) - Math.min(y, point_to.y)
  const pitch = Math.atan(pitch_width / height) * 180 / Math.PI
  return point(x, y, z, yaw, pitch)
}

/**
 * Convert a string to lowercase camel notion
 *
 * @param {string} string
 * @return {string}
 */
export const to_lower_camel = (string) => {
  not_null(string, 'A string value must exist')
  let out = ''
  let upper = false
  // eslint-disable-next-line no-restricted-syntax
  for (const c of string) {
    if (upper) { // uppercase the letter and rest it flag
      out += c.toUpperCase()
      upper = false
    } else if (c === '_') { // dont add _ and trigger next letter must be upper
      upper = true
    } else { // append the char
      out += c
    }
  }
  return out
}

/**
 Util functions and constants are provided for the aid of creating the JSON
 map objects. Since JSON may be tedious to use at times, helper functions are
 here to help with complex JSON objects that only need a few inputs.
 */
export default {
  point,
  point_and_look,
  to_lower_camel,
}
