/*
 * Copyright 2020 Year4000. All Rights Reserved.
 */

/** A generic command error */
export class CommandError extends Error {
}

/** A permission command error */
export class PermissionCommandError extends CommandError {
}

/** A option command error */
export class OptionCommandError extends CommandError {
}

/** A argument command error */
export class ArgumentCommandError extends CommandError {
}
