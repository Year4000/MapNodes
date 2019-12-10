/*
 * Copyright 2019 Year4000. All Rights Reserved.
 */

/** Create a point object with simpler syntax */
export const point = (x, y, z, yaw, pitch) => ({ 'point': { x, y, z, yaw, pitch } })

/** Generate the point object and use math to generate pitch and yaw */
export const point_and_look = (x, y, z, point_to) => {
  // todo fix the trig?
  let height = Math.max(x, point_to.point.x) - Math.min(x, point_to.point.x)
  let yaw_width = Math.max(z, point_to.point.z) - Math.min(z, point_to.point.z)
  let yaw = Math.atan(yaw_width / height) * 180 / Math.PI
  let pitch_width = Math.max(y, point_to.point.y) - Math.min(y, point_to.point.y)
  let pitch = Math.atan(pitch_width / height) * 180 / Math.PI
  return point(x, y, z, yaw, pitch)
}

/**
 Util functions and constants are provided for the aid of creating the JSON
 map objects. Since JSON may be tedious to use at times, helper functions are
 here to help with complex JSON objects that only need a few inputs.
 */
export default {
  point,
  point_and_look,
}
