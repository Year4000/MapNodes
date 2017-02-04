/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import com.google.gson.Gson;
import net.year4000.mapnodes.gson.Gsons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class Testing {
  private static final Bindings BINDINGS = new TestBindings();
  private static final String MAP = "{\n" +
    "  map: {\n" +
    "    name: () => 'Test Map',\n" +
    "    version: \"2.0\",\n" +
    "    description: \"Welcome to Year4000!\",\n" +
    "    authors: [\"98b35bed-9d73-47fe-a811-9436aa32335b\", \"bc3b90f1-2a7e-49ab-9241-81c1bf7dcf53\"]\n" +
    "  },\n" +
    "\n" +
    "  world: {\n" +
    "    spawn: [{point: {\"xyz\": \"0, 64, 0\"}}]\n" +
    "  },\n" +
    "\n" +
    "  games: {\n" +
    "    \"hub\": {}\n" +
    "  },\n" +
    "\n" +
    "  teams: {\n" +
    "    players: {\n" +
    "      name: \"Players\",\n" +
    "      color: () => { return ['a','n']},\n" +
    "      size: () => -1,\n" +
    "      spawns: [{point: {xyz: \"0, 64, 0\"}}]\n" +
    "    }\n" +
    "  }\n" +
    "}\n";

  public static void main(String[] args) throws Exception {
    String bindings = read(Testing.class.getResourceAsStream("/net/year4000/mapnodes/js/bindings.js"));
    try (V8ThreadLock<V8> thread = BINDINGS.v8Thread()) {
      thread.v8().executeVoidScript(bindings);
      thread.v8().executeScript("print('Hello');");
      thread.v8().executeScript("println(' World!');");
      thread.v8().executeScript("var_dump(PLATFORMS);");
      V8Object v8Object = thread.v8().executeObjectScript("eval(" + MAP + ");");
      Gson gson = Gsons.gsonBuilder().setPrettyPrinting().create();
      System.out.println(gson.toJson(v8Object));
    }
  }

  private static String read(InputStream input) throws IOException {
    try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
      return buffer.lines().collect(Collectors.joining("\n"));
    }
  }
}
