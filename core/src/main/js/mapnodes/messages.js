/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */

/** Translate the messages from the locale system */
const Messages = new Proxy({}, {
  get: (target, name, receiver) => {
    return {
      send: (player, args) => {
        $.bindings.send_locale_message(player.uuid, name, args || [])
      },
      get: (player, args) => {
        return $.bindings.get_locale_message(player.uuid, name, args || [])
      }
    }
  }
})

export default Messages
