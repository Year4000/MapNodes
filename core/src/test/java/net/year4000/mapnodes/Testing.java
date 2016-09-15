/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class Testing {
  private static final Bindings BINDINGS = new TestBindings();

  public static void main(String[] args) throws Exception {
    try {
      String bindings = read(Testing.class.getResourceAsStream("/js/bindings.js"));
      BINDINGS.v8().executeVoidScript(bindings);
      BINDINGS.v8().executeScript("print('Hello');");
      BINDINGS.v8().executeScript("println(' World!');");
      BINDINGS.v8().executeScript("var_dump(PLATFORMS);");
    } finally {
      BINDINGS.release();
    }
  }

  private static String read(InputStream input) throws IOException {
    try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
      return buffer.lines().collect(Collectors.joining("\n"));
    }
  }
}
