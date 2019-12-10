/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */

/** A generic command error */
export class CommandError extends Error {
  constructor() {
    super()
  }
}

/** A permission command error */
export class PermissionCommandError extends CommandError {
  constructor() {
    super()
  }
}

/** A option command error */
export class OptionCommandError extends CommandError {
  constructor() {
    super()
  }
}

/** A argument command error */
export class ArgumentCommandError extends CommandError {
  constructor() {
    super()
  }
}
