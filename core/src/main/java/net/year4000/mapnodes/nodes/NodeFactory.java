/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes.nodes;

import com.eclipsesource.v8.V8;

import java.util.Collection;

public interface NodeFactory {
  /** Get the runtime for the v8 engine */
  V8 v8();

  /** Create a node from the MapPackage */
  Node create(MapPackage map) throws Exception;

  /** Get the known map packages */
  Collection<MapPackage> packages();

  /** Generate the list of MapPackages */
  void generatePackages();
}
