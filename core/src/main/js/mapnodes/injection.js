/*
 * Copyright 2020 Year4000. All Rights Reserved.
 */
import _ from 'lodash'
import Logger from 'js-logger'
import { not_null } from './conditions.js'

/** The class to create an injector to inject other JavaScript objects */
export default class Injector {
  /** Create the injector and inject the modules with the other modules */
  constructor(modules) {
    not_null(modules, 'modules')
    // also inject the injector
    this._modules = { ...modules, injector: this }
    _.forEach(modules, (module) => this.inject_instance(module))
  }

  /** Inject the modules into the object prefixed with $ */
  inject_instance(instance) {
    not_null(instance, 'instance')
    if (typeof instance === 'object') {
      instance.constructor.$injector = this
    }
    return instance
  }

  /**
   * Create a child injector from this injector
   *
   * @param modules that will be merged with the parent injector
   * @return Injector The child injector injector
   */
  child_injector(modules) {
    return new Injector({ ...this._modules, ...modules })
  }

  /** Get the module */
  get_module(key) {
    return this._modules[key]
  }

  /** Get the value of this injector */
  toString() {
    return `modules: ${_.map(this._modules, (value, key) => `${key}::${value.toString()}`)}`
  }
}

// /** Create the class injector decorator */
// const class_inject = modules => handle => ({
//   ...handle,
//   elements: [...handle.elements, {
//     kind: 'field',
//     placement: 'static',
//     key: '$injector',
//     descriptor: {},
//     initializer: () => new Injector(modules),
//   }],
// })

// /** Create the property injector decorator */
// const property_inject = key => handle => ({
//   ...handle,
//   initializer: function() {
//     // Use proxy object to lazy get the values from the module
//     return new Proxy({}, {
//       get: (target, name, receiver) => {
//         try {
//           return this.constructor.$injector.get_module(key || handle.key)[name]
//         } catch (e) {
//           Logger.error('Error at function name ' + name + ' ' + receiver)
//           throw e
//         }
//       },
//     })
//   },
// })

/**
 * This will inject the variable from the type, it will.
 *
 * @param type The string key for the module or the object if a class inject
 * @return decorator for class or property
 */
// todo replace with new decorator descriptor system
// export const inject = type => (typeof type === 'object') ? class_inject(type) : property_inject(type)
export const inject = (type) => (target, key) => {
  if (typeof type === 'object') {
    return Object.defineProperty(target, '$injector', { value: new Injector(type) })
  }
  return Object.defineProperty(target, key, {
    get() {
      try {
        return target.constructor.$injector.get_module(key)
      } catch (e) {
        Logger.error(`Error at function name ${key}`)
        throw e
      }
    },
  })
}
