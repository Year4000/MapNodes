/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
import Logger from 'js-logger'
import Conditions from '../conditions.js'
import { CommandError } from './cmd_errors.js'
import CommandExecutor from './cmd_executor.js'

/** The command manager that will handle processing commands and ect */
export default class CommandManager {

  constructor() {
    this._command_map = {};
  }

  /** Register the command in to the system */
  register_command(command, action) {
    Conditions.not_null(command, 'command')
    Conditions.not_null(action, 'action')
    if (command in this._command_map) {
      Logger.warn(`Command ${command} exists not registering command...`)
    } else {
      this._command_map[command] = action
    }
  }

  /** Will execute the command and return a object for errors or null if it passed */
  execute_command(executor, command, args) {
    try {
      let command_action = this._command_map[command];
      if (command_action) {
        command_action(executor || new CommandExecutor(), args)
      } else {
        return new CommandError() // todo replace with command not found error
      }
    } catch (error) {
      if (error instanceof CommandError) {
        return error
      } else {
        throw error
      }
    }
  }

  /** Return true or false if the command is registered in mapnodes */
  is_command(command) {
    return this._command_map[command] != null
  }
}
