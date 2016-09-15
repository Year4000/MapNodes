/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes;

import net.year4000.mapnodes.nodes.Node;
import net.year4000.mapnodes.nodes.NodeFactory;
import net.year4000.mapnodes.nodes.NodeManager;

/** The system to handle the Maps and load their games */
public interface MapNodes {
  Settings SETTINGS = new Settings();
  NodeManager NODE_MANAGER = new NodeManager();

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

  }

  /** Enable the MapNodes system */
  default void enable() {

  }

  /** Unload the MapNodes system */
  default void unload() {

  }
}
