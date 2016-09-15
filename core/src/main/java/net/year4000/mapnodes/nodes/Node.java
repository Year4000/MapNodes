/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes.nodes;

import java.util.concurrent.atomic.AtomicInteger;

/** The node that contains the map object */
public abstract class Node {
  private static AtomicInteger idTracker = new AtomicInteger();
  private int id;

  public Node() {
    id = idTracker.getAndIncrement();
  }

  /** Get the internal id of this node */
  public int id() {
    return id;
  }

  /** Loads the node */
  public abstract void load();

  /** Unloads the node */
  public abstract void unload();
}
