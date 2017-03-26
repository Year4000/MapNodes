/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */

/** The class to create an injector to inject other JavaScript objects */
class Injector {

  /** Create the injector and inject the modules with the other modules */
  constructor(modules) {
    this._modules = Conditions.not_null(modules, 'modules')
    _.forEach(this._modules, (module, key) => this.inject_instance(module))
  }

  /** Inject the modules into the object prefixed with $ */
  inject_instance(instance) {
    Conditions.not_null(instance, 'instance')
    _.merge(instance, _.mapKeys(this._modules, (value, key) => `$${key}`))
  }

  /** Create a child injector from this injector */
  child_injector(modules, injected) {
    if (injected) {
      return injected.$injector = new Injector(_.merge(modules, this._modules))
    }
    return new Injector(_.merge(modules, this._modules))
  }

  /** Get the value of this injector */
  // Override
  toString() {
    return `modules: ${_.map(this._modules, (value, key) => `${key}::${value.toString()}`)}`
  }
}
