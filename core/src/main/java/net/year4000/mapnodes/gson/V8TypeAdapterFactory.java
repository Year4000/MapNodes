/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes.gson;

import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import net.year4000.mapnodes.gson.adapters.V8ArrayTypeAdapter;
import net.year4000.mapnodes.gson.adapters.V8ObjectTypeAdapter;

/** This will register the V8 adapters for Gson */
public class V8TypeAdapterFactory implements TypeAdapterFactory {
  @Override
  @SuppressWarnings("unchecked")
  public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
    if (type.getType() == V8Array.class) {
      return (TypeAdapter<T>) V8ArrayTypeAdapter.INSTANCE;
    } else if (type.getType() == V8Object.class) {
      return (TypeAdapter<T>) V8ObjectTypeAdapter.INSTANCE;
    }
    return null;
  }
}
