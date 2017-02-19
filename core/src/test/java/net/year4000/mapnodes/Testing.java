/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.year4000.mapnodes.gson.V8TypeAdapterFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class Testing {
  private static final Bindings BINDINGS = new TestBindings();

  public static void main(String[] args) throws Exception {
    String bindings = read(Testing.class.getResourceAsStream("/net/year4000/mapnodes/js/bindings.js"));
    String map = read(Testing.class.getResourceAsStream("/map.js"));
    try (V8ThreadLock<V8> thread = BINDINGS.v8Thread()) {
      thread.v8().executeVoidScript(bindings);
      thread.v8().executeScript("print('Hello');");
      thread.v8().executeScript("println(' World!');");
      thread.v8().executeScript("var_dump(PLATFORMS);");
      V8Object v8Object = thread.v8().executeObjectScript("eval(" + map + ");");
      Gson gson = new GsonBuilder().registerTypeAdapterFactory(new V8TypeAdapterFactory()).setPrettyPrinting().create();
      System.out.println(gson.toJson(v8Object));
    }
  }

  private static String read(InputStream input) throws IOException {
    try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
      return buffer.lines().collect(Collectors.joining("\n"));
    }
  }
}
