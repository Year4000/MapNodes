/*
 * Copyright 2020 Year4000. All Rights Reserved.
 */

/** @typedef {import('./game/player').default} Player */

/** Translate the messages from the locale system */
/** @type {{ [key: string]: { get(Player, ...any), send(Player, ...any) } }} */
const Messages = new Proxy({}, {
  get: (target, name) => ({
    /**
     * Send the translated message to the player
     *
     * @param {Player} player
     * @param {...any} args
     */
    send(player, ...args) {
      $.bindings.send_locale_message(player.uuid, name, args ?? [])
    },
    /**
     * Get the translated message from the player set locale
     *
     * @param {Player} player
     * @param {...any} args
     * @return {string}
     */
    get(player, ...args) {
      return $.bindings.get_locale_message(player.uuid, name, args ?? [])
    },
  }),
})

export default Messages
