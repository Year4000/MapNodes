/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes.nodes;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import net.year4000.mapnodes.V8ThreadLock;
import net.year4000.utilities.ErrorReporter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/** The node that contains the map object */
public abstract class Node {
  private static AtomicInteger idTracker = new AtomicInteger();
  protected final V8Object v8Object;
  protected final MapPackage map;
  protected final int id;

  /** Create the node */
  public Node(NodeFactory factory, MapPackage map) throws Exception {
    id = idTracker.getAndIncrement();
    this.map = map;
    try (BufferedReader buffer = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(map.map().array())))) {
      String script = buffer.lines().collect(Collectors.joining("\n"));
      try (V8ThreadLock<V8> lock = factory.v8Thread()) {
        this.v8Object = lock.v8().executeObjectScript("eval(" + script + ");");
      }
    } catch (IOException | NullPointerException error) {
      throw ErrorReporter.builder(error).buildAndReport(System.err);
    }
  }

  /** Get the MapPackage instance */
  public MapPackage map() {
    return map;
  }

  /** Get the name of the map */
  public String name() {
    try (V8ThreadLock<V8Object> lock = new V8ThreadLock<>(v8Object)) {
      return lock.v8().getObject("map").getString("name");
    } catch (Exception error) {
      ErrorReporter.builder(error).hideStackTrace().buildAndReport(System.err);
      return "unknown";
    }
  }

  /** Get the version of the map */
  public String version() {
    try (V8ThreadLock<V8Object> lock = new V8ThreadLock<>(v8Object)) {
      return lock.v8().getObject("map").getString("version");
    } catch (Exception error) {
      ErrorReporter.builder(error).hideStackTrace().buildAndReport(System.err);
      return "unknown";
    }
  }

  /** Get the internal id of this node */
  public int id() {
    return id;
  }

  /** Loads the node */
  public abstract void load();

  /** Unloads the node */
  public void unload() {
    try (V8ThreadLock<V8Object> lock = new V8ThreadLock<>(v8Object)) {
      lock.v8().release();
    }
  }
}
