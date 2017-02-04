/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes.gson.adapters;

import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.V8Value;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/** V8 Object to JSON Object */
public class V8ObjectTypeAdapter extends AbstractV8TypeAdapter<V8Object> {
  public static final V8ObjectTypeAdapter INSTANCE = new V8ObjectTypeAdapter();

  @Override
  public void write(JsonWriter out, V8Object value) throws IOException {
    out.beginObject();
    for (String key : value.getKeys()) {
      out.name(key);
      int type = value.getType(key);
      if (V8_TYPE_MAP.containsKey(type)) { // Handle standard types
        V8_TYPE_MAP.get(type).handle(out, value, key);
      } else {
        throw new IOException("Unknown type for key: " + type);
      }
    }
    out.endObject();
  }
}
