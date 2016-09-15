/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */

/**
  Bindings is the bridge between the multiple languages. The bindings allows
  for object programing, but still allow the speed that is needed. Since only
  the low level forms the data types are transmitted between.
*/

/** This constant is created by the runtime no need for it */
var PLATFORM = _PLATFORM;

/** The constants that are known when the JS runtime is created */
var PLATFORMS = {
  "PC": "java", // Sponge
  "PE": "java" // Nukkit
};

/** Used to interact the Javascript with MapNodes base game */
class $ {
  /** Map a specific object to this bindings var */
  static get bindings() {
    if (PLATFORM == PLATFORMS.PE) {
      return JAVA || {};
    } else if (PLATFORM == PLATFORMS.PC) {
      return JAVA || {};
    } else {
      throw "PLATFORM is not defined!";
    }
  }

  /** Send a message to the player */
  static send_message(player, message) {
      $.bindings.send_message(player.uuid, message);
  }
}

/** Print the message to the console */
function print(message) {
  $.bindings.print(message);
}

/** Print a line to the console */
function println(message) {
  print(message + "\n")
}

/** Dump the var to the screen */
function var_dump(variable) {
  println(JSON.stringify(variable));
}