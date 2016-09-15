/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes.nodes;

import java.util.Collection;

public interface NodeFactory {
  /** Create a node from the MapPackage */
  Node create(MapPackage map);

  /** Generate the known map packages */
  Collection<MapPackage> packages();
}
