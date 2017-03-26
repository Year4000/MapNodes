/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
'use strict'

/** The command executor is the one who is executating the commands */
class CommandExecutor {

    constructor(executor) {
      this._executor = executor
    }

    /** Send the message to the executor */
    send_message(message) {
      if (this._executor) {
        return $.bindings.send_message(this._executor, message)
      }
      return println(message)
    }

    /** Does the executor has a permission */
    has_permission(permission) {
      if (this._executor) {
        return $.bindings.has_permission(this._executor, permission)
      }
      return true
    }
}
