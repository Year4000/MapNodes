/*
 * Copyright 2020 Year4000. All Rights Reserved.
 */
import Logger from 'js-logger'


/** Return a useless function */
const useless_function = () => {}


/**
 * The proxy object that will forwards the bindings to the underlying system
 *
 * @type {{ [key: string]: (...any) => any }}
 */
const bindings = new Proxy(global.PLATFORM, {
  get(target, name) {
    if (target[name]) {
      return (...args) => {
        try {
          return target[name](...args)
        } catch (any) {
          Logger.error('An error has been thrown')
          Logger.error(any)
          return useless_function
        }
      }
    }
    Logger.error(`${name} has not been defined in the bindings mappings`)
    return useless_function
  },
})

export default bindings
