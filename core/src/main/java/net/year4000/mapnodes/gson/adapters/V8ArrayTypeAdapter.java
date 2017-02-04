/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes.gson.adapters;

import com.eclipsesource.v8.V8Array;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/** Create the JSON for the array */
public class V8ArrayTypeAdapter extends AbstractV8TypeAdapter<V8Array> {
  public static final V8ArrayTypeAdapter INSTANCE = new V8ArrayTypeAdapter();

  @Override
  public void write(JsonWriter out, V8Array value) throws IOException {
    out.beginArray();
    for (int i = 0; i < value.length(); i++) {
      int type = value.getType(i);
      if (V8_TYPE_MAP.containsKey(type)) { // Handle standard types
        V8_TYPE_MAP.get(type).handle(out, value, String.valueOf(i));
      } else {
        throw new IOException("Unknown type for key: " + type);
      }
    }
    out.endArray();
  }
}
