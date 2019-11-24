import assert from 'assert'
import JsonObject from './json_object.js'

class SchemaTest extends JsonObject {
  static get schema() {
    return {
      foo: { type: 'string', value: 'bar' }
    }
  }
}

describe('json object', () => {
  let schema_object = new SchemaTest('test', {
    foo: 'bar'
  })

  it('schema must exist', () => assert.ok(SchemaTest.schema))

  it('should schema validate', () => assert.ok(schema_object.verify()))

  it('should schema not ok', () => assert.ok(!schema_object.verify({ foo: 42 })))
})
