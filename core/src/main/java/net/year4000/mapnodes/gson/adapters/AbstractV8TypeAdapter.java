/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes.gson.adapters;

import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.V8Value;
import com.google.common.collect.ImmutableMap;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import net.year4000.mapnodes.MapNodes;

import java.io.IOException;

/** This is the base for the V8 adapter */
public abstract class AbstractV8TypeAdapter<T extends V8Value> extends TypeAdapter<T> {
  /** Handle the class types for the V8 Function bellow */
  protected static final ImmutableMap<Class<?>, ValueHandler<Object>> TYPE_MAP = ImmutableMap.<Class<?>, ValueHandler<Object>>builder()
    .put(String.class, (out, value, unused) -> out.value((String) value))
    .put(Integer.class, (out, value, unused) -> out.value((int) value))
    .put(Boolean.class, (out, value, unused) -> out.value((boolean) value))
    .put(Double.class, (out, value, unused) -> out.value((double) value))
    .put(V8Object.class, (out, value, unused) -> MapNodes.GSON.toJson(value, V8Object.class, out))
    .put(V8Array.class, (out, value, unused) -> MapNodes.GSON.toJson(value, V8Array.class, out))
    .build();
  /** Map the V8Value types to handler that will write the JSON */
  protected static final ImmutableMap<Integer, ValueHandler<V8Object>> V8_TYPE_MAP = ImmutableMap.<Integer, ValueHandler<V8Object>>builder()
    .put(V8Value.NULL, (out, value, key) -> out.nullValue())
    .put(V8Value.UNDEFINED, (out, value, key) -> out.nullValue())
    .put(V8Value.BOOLEAN, (out, value, key) -> out.value(value.getBoolean(key)))
    .put(V8Value.STRING, (out, value, key) -> out.value(value.getString(key)))
    .put(V8Value.INTEGER, (out, value, key) -> out.value(value.getInteger(key)))
    .put(V8Value.BYTE, (out, value, key) -> out.value(value.getInteger(key)))
    .put(V8Value.DOUBLE, (out, value, key) -> out.value(value.getDouble(key)))
    .put(V8Value.V8_OBJECT, (out, value, key) -> V8ObjectTypeAdapter.INSTANCE.write(out, value.getObject(key)))
    .put(V8Value.V8_ARRAY, (out, value, key) -> V8ArrayTypeAdapter.INSTANCE.write(out, value.getArray(key)))
    .put(V8Value.V8_FUNCTION, (out, value, key) -> {
      Object object = value.executeJSFunction(key);
      Class<?> clazz = object.getClass();
      if (TYPE_MAP.containsKey(clazz)) {
        TYPE_MAP.get(clazz).handle(out, object);
      } else {
        out.nullValue();
      }
    })
    .build();

  @Override
  public T read(JsonReader in) throws IOException {
    throw new IOException("You can only go from V8 to JSON");
  }
}
