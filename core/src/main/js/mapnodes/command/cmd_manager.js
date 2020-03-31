/*
 * Copyright 2020 Year4000. All Rights Reserved.
 */
import Logger from 'js-logger'

import CommandExecutor from './cmd_executor.js'
import { not_null } from '../conditions.js'
import { CommandError } from './cmd_errors.js'


/** @typedef {(CommandExecutor, any) => any} CommandAction */

/** The command manager that will handle processing commands and ect */
export default class CommandManager {
  constructor() {
    /** @type {{ [key: string]: CommandAction }} */
    this._command_map = {}
  }

  /**
   * Register the command in to the system
   *
   * @param {string} command
   * @param {CommandAction} action
   */
  register_command(command, action) {
    not_null(command, 'command')
    not_null(action, 'action')
    if (command in this._command_map) {
      Logger.warn(`Command ${command} exists not registering command...`)
    } else {
      this._command_map[command] = action
    }
  }

  /**
   * Will execute the command and return a object for errors or null if it passed
   *
   * @param {CommandExecutor} executor
   * @param {string} command
   * @param args
   * @return {CommandError | undefined}
   */
  execute_command(executor, command, args) {
    try {
      const command_action = this._command_map[command]
      if (command_action) {
        command_action(executor ?? new CommandExecutor(), args)
      } else {
        return new CommandError() // todo replace with command not found error
      }
    } catch (error) {
      if (error instanceof CommandError) {
        return error
      }
      throw error
    }
    return undefined
  }

  /**
   * Return true or false if the command is registered in mapnodes
   *
   * @param {string} command
   * @return {boolean}
   */
  is_command(command) {
    return this._command_map[command] != null
  }
}
