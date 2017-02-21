/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */

/**
  Util functions and constants are provided for the aid of creating the JSON
  map objects. Since JSON may be tedious to use at times, helper functions are
  here to help with complex JSON objects that only need a few inputs.
*/

/** Create a point object with simpler syntax */
function point(x, y, z, yaw, pitch) {
  return {"point": {"x": x, "y": y, "z": z, "yaw": yaw, "pitch": pitch}};
}

/** Generate the point object and use math to generate pitch and yaw */
function point_and_look(x, y, z, point_to) {
  // todo fix the trig?
  var height = Math.max(x, point_to.point.x) - Math.min(x, point_to.point.x);
  var yaw_width = Math.max(z, point_to.point.z) - Math.min(z, point_to.point.z);
  var yaw = Math.atan(yaw_width / height) * 180 / Math.PI;
  var pitch_width = Math.max(y, point_to.point.y) - Math.min(y, point_to.point.y);
  var pitch = Math.atan(pitch_width / height) * 180 / Math.PI;
  return point(x, y, z, yaw, pitch);
}
