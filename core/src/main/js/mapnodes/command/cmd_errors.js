/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
'use strict'

/** A generic command error */
class CommandError extends Error {
  constructor() {
    super()
  }
}

/** A permission command error */
class PermissionCommandError extends CommandError {
  constructor() {
    super()
  }
}

/** A option command error */
class OptionCommandError extends CommandError {
  constructor() {
    super()
  }
}

/** A argument command error */
class ArgumentCommandError extends CommandError {
  constructor() {
    super()
  }
}
