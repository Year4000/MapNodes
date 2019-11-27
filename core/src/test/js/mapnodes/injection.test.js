import Injector, { inject } from './injection.js'
import assert from 'assert'

describe('injectors', () => {
  it('injects', () => {
    @inject({
      test: { foo: 'bar' }
    })
    class Test {
      @inject('test') test
    }

    assert.equal(new Test().test.foo, 'bar')
  })
  const obj = new Injector({ foo: 'bar', test: {} }).inject_instance({ child: {} })
  it('child inject', () => obj.constructor.$injector.inject_instance(obj.child))
})
