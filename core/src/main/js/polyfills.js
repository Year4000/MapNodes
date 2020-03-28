/*
 * Copyright 2020 Year4000. All Rights Reserved.
 */


// Only create console object if it does not exists
if (!('console' in global)) {
  /** Map some console properties for logging */
  global.console = {
    log: (...args) => JAVA.print(`${args}\n`),
    info: (...args) => console.log(...args),
    warn: (...args) => console.log(...args),
    error: (...args) => console.log(...args),
    debug: (...args) => console.log(...args),
  }
}
