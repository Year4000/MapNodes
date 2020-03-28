/*
 * Copyright 2020 Year4000. All Rights Reserved.
 */

/** Translate the messages from the locale system */
const Messages = new Proxy({}, {
  get: (target, name, receiver) => ({
    send: (player, args) => {
      $.bindings.send_locale_message(player.uuid, name, args ?? [])
    },
    get: (player, args) => $.bindings.get_locale_message(player.uuid, name, args ?? []),
  }),
})

export default Messages
