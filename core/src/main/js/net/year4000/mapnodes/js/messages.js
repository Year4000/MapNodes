/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
'use strict'

/** Translate the messages from the locale system */
const Messages = new Proxy({}, {
  get: (target, name, receiver) => {
    // todo translate the message for the specific player, also provide an option to send the message.
  }
})
