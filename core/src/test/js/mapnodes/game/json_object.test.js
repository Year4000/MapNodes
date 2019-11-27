import assert from 'assert'
import JsonObject from './json_object.js'

class SchemaTest extends JsonObject {
  static get schema() {
    return {
      foo: { type: 'string', value: 'bar' },
      options: { type: ['string', 'number'], value: 'bar' },
      values: { type: 'array', value: [] }
    }
  }
}

const test_schema = obj => new SchemaTest('test', {}).verify(obj)

describe('json object', () => {
  it('schema must exist', () => assert.ok(SchemaTest.schema))
  it('should schema validate', () => assert.ok(test_schema({
    foo: 'bar',
    values: [],
    options: 0,
  })))
  it('should or schema validate', () => assert.ok(test_schema({
    options: 'foobar'
  })))
  it('should schema not ok', () => assert.ok(!test_schema({
    foo: 42
  })))
  it('should or schema not ok', () => assert.ok(!test_schema({
    options: false
  })))
})
