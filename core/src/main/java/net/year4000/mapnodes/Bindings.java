/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes;

import com.eclipsesource.v8.*;
import com.eclipsesource.v8.utils.MemoryManager;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Queues;
import com.google.common.io.Files;
import com.google.inject.Inject;
import net.year4000.utilities.Conditions;
import net.year4000.utilities.ErrorReporter;
import org.slf4j.Logger;

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
    V8.setFlags("--harmony");
  }
  /** The V8 Runtime for everything */
  private static V8 engine = V8.createV8Runtime("global", Files.createTempDir().getAbsolutePath());
  /** The memory manager for any javascript object that were created */
  private static MemoryManager memoryManager = new MemoryManager(engine);
  /** The handler to interact with the Javascript object */
  protected final InvocationHandler handler = new V8InvocationHandler(engine, "$.js");
  /** The V8 Object that is bind to the JAVA var */
  private final V8Object object;
  /** Paths that need to be included after import */
  private final ArrayDeque<String> paths = Queues.newArrayDeque();
  /** Inject the logger used in the application */
  @Inject private Logger logger;
  /** Inject the setting for use in the application */
  @Inject private Settings settings;

  /** Map the java methods to the javascript functions */
  protected Bindings() {
    object = new V8Object(engine);
    for (Method method : getClass().getMethods()) {
      if (method.getAnnotation(Bind.class) != null) {
        String lower = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, method.getName());
        object.registerJavaMethod(this, method.getName(), lower, method.getParameterTypes());
      }
    }
    engine.add("PLATFORM", "java");
    engine.add("JAVA", object);
    engine.getLocker().acquire(); // acquire the lock because idk
  }

  /** Get the v8 instance */
  public V8 v8() {
    return engine;
  }

  /** Release the bindings */
  @Override
  public void release() {
    memoryManager.release();
  }

  /** $.bindings.debug */
  @Bind
  public boolean debug() {
    return settings.debug;
  }

  /** $.bindings.v8_version */
  @Bind
  public String v8Version() {
    return V8.getV8Version();
  }

  /** $.bindings.print */
  @Bind
  public void print(String message) {
    logger.info(message.replaceAll("\n", ""));
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
    logger.info("Loading javascript file: " + Conditions.nonNullOrEmpty(path, "path"));
    InputStream stream = Bindings.class.getResourceAsStream(path);
    try (BufferedReader buffer = new BufferedReader(new InputStreamReader(stream))) {
      String script = buffer.lines().collect(Collectors.joining("\n"));
      engine.executeVoidScript(script);
    } catch (IOException | NullPointerException error) {
      logger.error(ErrorReporter.builder(error).add("path: ", path).build().toString());
    } finally {
      // Check for pending imports
      if (!paths.isEmpty()) {
        include(paths.pop());
      }
    }
  }

  /** $.bindings.send_message */
  @Bind public abstract void sendMessage(String player, String message);

  /** $.bindings.player_meta_uuid */
  @Bind public abstract String playerMetaUuid(String uuid);

  /** Run function from the JavaScript side */
  public interface V8Bindings {
    /** $.js.platform_name */
    String platformName();

    /** $.js.is_game_running */
    boolean isGameRunning();

    /** $.js.load */
    void load();

    /** $.js.on_event */
    void onEvent(String id, String event);

    /** $.js.swap_game */
    void swapGame(int id, V8Object map);

    /** $.js.start */
    void start();

    /** $.js.stop */
    void stop();

    /** $.js.game_state */
    String gameState();

    /** $.js.join_game */
    void joinGame(String uuid);

    /** $.js.leave_game */
    void leaveGame(String uuid);

    /** $.js.spawn_point */
    V8Array spawnPoint();

    /** $.js.last_spawn_point */
    V8Array lastSpawnPoint();

    /** $.js.on_player_command */
    V8Object onPlayerCommand(String uuid, String username, String command, String args);

    /** $.js.is_command */
    boolean isCommand(String command);
  }
}
