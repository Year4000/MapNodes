/*
 * Copyright 2020 Year4000. All Rights Reserved.
 */

/**
 * Map some console properties for logging
 * Only create console object if it does not exists
 */
global.console = global.console ?? {
  log: (...args) => global.PLATFORM.print(`${args}\n`),
  info: (...args) => console.log(...args),
  warn: (...args) => console.log(...args),
  error: (...args) => console.log(...args),
  debug: (...args) => console.log(...args),
}
