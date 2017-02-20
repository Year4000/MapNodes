/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes;

import com.eclipsesource.v8.Releasable;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.utils.MemoryManager;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Queues;
import com.google.common.io.Files;
import net.year4000.utilities.Conditions;
import net.year4000.utilities.ErrorReporter;

import java.io.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.stream.Collectors;

/** Create the needed bindings for the javascript functions */
public abstract class Bindings implements Releasable {
  /** Allow method of this class to be bind to the JAVA var in JavaScript */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  @interface Bind {}
  // Set the flags for the V8 runtime
  static {
    V8.setFlags("--harmony --use_strict");
  }
  /** The V8 Runtime for everything */
  private static V8 engine = V8.createV8Runtime("mapnodes", Files.createTempDir().getAbsolutePath());
  /** The memory manager for any javascript object that were created */
  private static MemoryManager memoryManager = new MemoryManager(engine);
  /** The V8 Object that is bind to the JAVA var */
  private final V8Object object;
  /** Paths that need to be included after import */
  private final ArrayDeque<String> paths = Queues.newArrayDeque();
  /** The handler to interact with the Javascript object */
  protected final InvocationHandler handler = new V8InvocationHandler(engine);

  /** Map the java methods to the javascript functions */
  protected Bindings() {
    try (V8ThreadLock<V8> lock = v8Thread()) {
      object = new V8Object(engine);
      for (Method method : getClass().getMethods()) {
        if (method.getAnnotation(Bind.class) != null) {
          String lower = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, method.getName());
          object.registerJavaMethod(this, method.getName(), lower, method.getParameterTypes());
        }
      }
      lock.v8().add("PLATFORM", "java");
      lock.v8().add("JAVA", object);
    }
  }

  /** Get the v8 instance */
  public V8ThreadLock<V8> v8Thread() {
    return new V8ThreadLock<>(engine);
  }

  /** Release the bindings */
  @Override
  public void release() {
    try (V8ThreadLock<V8> lock = v8Thread()) {
      memoryManager.release();
      lock.v8().release();
    }
  }

  /** $.bindings.print */
  @Bind
  public void print(String message) {
    System.out.print(message);
  }

  /** $.bindings._include */
  @Bind
  public void _include(String path) {
    Conditions.nonNullOrEmpty(path, "path");
    if (!engine.isReleased()) {
      paths.add(path);
    } else {
      include(path);
    }
  }

  /** $.bindings.include */
  @Bind
  public void include(String path) {
    // Include the system
    System.out.println("Loading javascript file: " + Conditions.nonNullOrEmpty(path, "path"));
    InputStream stream = Bindings.class.getResourceAsStream(path);
    try (BufferedReader buffer = new BufferedReader(new InputStreamReader(stream))) {
      String script = buffer.lines().collect(Collectors.joining("\n"));
      try (V8ThreadLock<V8> lock = v8Thread()) {
        lock.v8().executeVoidScript(script);
      }
    } catch (IOException | NullPointerException error) {
      System.err.println(ErrorReporter.builder(error).add("path: ", path).build().toString());
    } finally {
      // Check for pending imports
      if (!paths.isEmpty()) {
        include(paths.pop());
      }
    }
  }

  /** $.bindings.send_message */
  @Bind public abstract void sendMessage(String player, String message);

  /** Run function from the JavaScript side */
  public interface V8Bindings {
    /** $.js.platform_name */
    String platformName();

    /** $.js.is_game_running */
    boolean isGameRunning();

    /** $.js.load */
    void load();
  }
}
