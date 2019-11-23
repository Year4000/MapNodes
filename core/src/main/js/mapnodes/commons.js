/*
 * Copyright 2019 Year4000. All Rights Reserved.
 */
import _ from 'lodash'

/** Register all listeners into the event emitter */
export const register_listeners = (clazz, event_emitter, instance) => {
  // you must register the functions static, then before we register them into the system
  // they are binded to this class
  Reflect.ownKeys(clazz).filter(name => _.endsWith(name, '$listener')).forEach(name => {
    let event_name = name.substring(0, name.indexOf('$'))
    let listener = Reflect.get(clazz, name)
    event_emitter.on(event_name, listener.bind(instance))
  })
}

/** This contains the common functions that are needed in MapNodes */
export default {
  register_listeners,
}
