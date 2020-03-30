import { event_manager, listener } from './event_manager.js' // eslint-disable-line import/no-unresolved


describe('event manager', () => {
  it('register event', () => {
    // Create a dummy class where its listeners are registered in the event manager
    class Events {
      @listener('test')
      static on_test(event) {
        return event
      }
    }
  })
  it('trigger event', () => event_manager.trigger('test', ['value']))
})
