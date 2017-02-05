/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.year4000.mapnodes.gson.V8TypeAdapterFactory;
import net.year4000.mapnodes.nodes.Node;
import net.year4000.mapnodes.nodes.NodeFactory;
import net.year4000.mapnodes.nodes.NodeManager;
import net.year4000.utilities.ErrorReporter;
import org.slf4j.Logger;

/** The system to handle the Maps and load their games */
public interface MapNodes {
  Settings SETTINGS = new Settings();
  NodeManager NODE_MANAGER = new NodeManager();
  Gson GSON = new GsonBuilder()
    .setVersion(3.0)
    .registerTypeAdapterFactory(new V8TypeAdapterFactory())
    .create();

  /** Get the logger for the system */
  Logger logger();

  /** Get the bindings */
  Bindings bindings();

  /** Get the node factory */
  NodeFactory nodeFactory();

  /** Get the current node */
  default Node currentNode() {
    return NODE_MANAGER.getNode();
  }

  /** Load the MapNodes system */
  default void load() {
    // Inject the javascript files into v8
    logger().info("Loading javascript files into v8 runtime");
    // Load just the bindings and the bootstrap it will handle the rest
    ImmutableList.of("bindings.js", "bootstrap.js").forEach(file -> bindings().include(file));
    // Generate the maps
    logger().info("Generating map packages");
    nodeFactory().generatePackages();
  }

  /** Enable the MapNodes system */
  default void enable() {
    logger().info("Adding maps to queue (max: " + SETTINGS.loadMaps + ")");
    nodeFactory().packages().forEach(map -> {
      logger().info("Adding map: " + map.toString());
      try {
        Node node = nodeFactory().create(map);
        logger().info("Map " + node.name() + " version " + node.version());
        NODE_MANAGER.addToQueue(node);
      } catch (Exception error) {
        logger().error(ErrorReporter.builder(error).build().toString());
      }
    });
    // Set up the first node if we can
    if (nodeFactory().packages().size() > 0) {
      logger().info("Creating first node");
      try {
        Node node = NODE_MANAGER.loadNextNode();
        logger().info("Map " + node.name() + " version " + node.version());
      } catch (Exception error) {
        logger().error(ErrorReporter.builder(error).add("Could not load map").build().toString());
      }
    }
  }

  /** Unload the MapNodes system */
  default void unload() {
    // Unload the last map in the system
    if (nodeFactory().packages().size() > 0) {
      logger().info("Unloading current node");
      try {
        currentNode().unload();
      } catch (Exception error) {
        logger().error(ErrorReporter.builder(error).add("Could not unload map").build().toString());
      }
    }
    logger().info("Releasing runtime bindings");
    bindings().release();
  }
}
