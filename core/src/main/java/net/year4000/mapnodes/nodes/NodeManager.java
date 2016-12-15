/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes.nodes;

import com.google.common.collect.Queues;
import net.year4000.utilities.ErrorReporter;
import net.year4000.utilities.value.Value;

import java.util.Queue;

/** The node manager system to load the maps and there game */
public final class NodeManager {
  private final Queue<Node> queue = Queues.newLinkedBlockingQueue();
  private Node current;

  /** Get the current node or throw null pointer exception */
  public Node getNode() {
    if (current == null) {
      try {
        current = loadNextNode();
      } catch (Exception error) {
        ErrorReporter.builder(error).hideStackTrace().buildAndReport(System.err);
        return null;
      }
    }
    return current;
  }

  /** Load the next node or throw null pointer exception */
  public Node loadNextNode() throws Exception {
    if (isNextNode()) {
      current = nextNode().getOrThrow("No nodes available");
      current.load();
    }
    return current;
  }

  /** Get the next node if there are any also clean up the last node */
  private Value<Node> nextNode() {
    if (current != null) {
      try {
        current.unload();
      } catch (Exception error) {
        ErrorReporter.builder(error).hideStackTrace().buildAndReport(System.err);
      }
    }
    return Value.of(queue.poll());
  }

  /** Check if there are more nodes */
  private boolean isNextNode() {
    return queue.size() > 0;
  }

  /** Add the node to the queue */
  public boolean addToQueue(Node node) {
      return queue.add(node);
  }
}
