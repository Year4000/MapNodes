/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes;

import com.eclipsesource.v8.V8;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.year4000.mapnodes.gson.V8TypeAdapterFactory;
import net.year4000.mapnodes.nodes.Node;
import net.year4000.mapnodes.nodes.NodeFactory;
import net.year4000.mapnodes.nodes.NodeManager;
import net.year4000.utilities.ErrorReporter;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

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
    ImmutableSet.of("bindings.js", "game.js", "player.js", "team.js", "utils.js").forEach(file -> {
      logger().info("Loading javascript file: " + file);
      InputStream stream = MapNodes.class.getResourceAsStream("/net/year4000/mapnodes/js/" + file);
      try (BufferedReader buffer = new BufferedReader(new InputStreamReader(stream))) {
        String script = buffer.lines().collect(Collectors.joining("\n"));
        try (V8ThreadLock<V8> lock = bindings().v8Thread()) {
          lock.v8().executeVoidScript(script);
        }
      } catch (IOException | NullPointerException error) {
        logger().error(ErrorReporter.builder(error).add("file: ", file).build().toString());
      }
    });
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
