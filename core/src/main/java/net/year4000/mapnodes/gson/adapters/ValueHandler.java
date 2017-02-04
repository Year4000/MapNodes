/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes.gson.adapters;

import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public interface ValueHandler<T> {
  /** Handle the JSON writer from the value and the key */
  void handle(JsonWriter out, T value, String key) throws IOException;

  /** Handle the writing for the object */
  default void handle(JsonWriter out, T value) throws IOException {
    handle(out, value, null);
  }
}
