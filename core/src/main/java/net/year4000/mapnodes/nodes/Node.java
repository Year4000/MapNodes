/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes.nodes;

import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.utils.MemoryManager;
import net.year4000.mapnodes.MapNodes;
import net.year4000.mapnodes.game.InfoComponent;
import net.year4000.utilities.ErrorReporter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * The node that contains the map object.
 *
 * Important thing to note about the V8 object and the memory manager.
 * During the creation of the node, we load the map into the v8object.
 * At the end of the nodes life time we will release all objects created.
 */
public abstract class Node {
  private static AtomicInteger idTracker = new AtomicInteger(1);
  private final InfoComponent info;
  protected MemoryManager memoryManager;
  protected final V8Object v8Object;
  protected final MapPackage map;
  protected final int id;

  /**
   * Create the node, we are just testing the validity of the map
   * and generating the facts we need before starting the node.
   */
  public Node(NodeFactory factory, MapPackage map) throws Exception {
    id = idTracker.getAndIncrement();
    this.map = map;
    try (BufferedReader buffer = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(map.map().array())))) {
      String script = buffer.lines().collect(Collectors.joining("\n"));
      v8Object = factory.v8().executeObjectScript("eval(" + script + ");");
      // Create a memory manager, gather facts about the map then release the memory manager
      memoryManager = new MemoryManager(factory.v8());
      info = MapNodes.GSON.fromJson(MapNodes.GSON.toJsonTree(v8Object.getObject("map")), InfoComponent.class);
      memoryManager.release();
    } catch (IOException | NullPointerException error) {
      throw ErrorReporter.builder(error).add("Node id", id).buildAndReport(System.err);
    }
  }

  /** Get the MapPackage instance */
  public MapPackage map() {
    return map;
  }

  /** Get the name of the map */
  public String name() {
    return info.name();
  }

  /** Get the version of the map */
  public String version() {
    return info.version();
  }

  /** Get the internal id of this node */
  public int id() {
    return id;
  }

  /** Is this node the current node */
  public boolean isCurrentNode() {
    return MapNodes.NODE_MANAGER.isCurrentNode(id);
  }

  /** Loads the node */
  public abstract void load() throws Exception;

  /** Unloads the node */
  public void unload() throws Exception {
    if (memoryManager != null) {
      memoryManager.release(); // release any object's created during the existence of this node
    }
    v8Object.release(); // release the object that the map.js is stored in
  }
}
