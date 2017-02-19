/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */

/** The class to create an injector to inject other JavaScript objects */
class Injector {

  /** Create the injector and inject the modules with the other modules */
  constructor(modules) {
    this._modules = Conditions.not_null(modules, 'modules');
    _.forEach(this._modules, (module, key) => this.inject_instance(module));
  }

  /** Inject the modules into the object prefixed with $ */
  inject_instance(instance) {
    Conditions.not_null(instance, 'instance');
    _.merge(instance, _.reduce(this._modules, (result, value, key) => {
      result['$' + key] = value;
      return result;
    }));
    instance['$injector'] = this; // always replace the injector with ourselves
  }

  /** Create a child injector from this injector */
  child_injector(modules) {
    return new Injector(_.merge(modules, this._modules));
  }
}
