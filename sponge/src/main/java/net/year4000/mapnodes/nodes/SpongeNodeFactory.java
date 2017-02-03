/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes.nodes;

import com.eclipsesource.v8.V8;
import com.google.common.collect.ImmutableSet;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.V8ThreadLock;

import java.io.IOException;
import java.util.Collection;

public class SpongeNodeFactory implements NodeFactory {
  @Override
  public Node create(MapPackage map) throws Exception {
    return new SpongeNode(this, map);
  }

  @Override
  public Collection<MapPackage> packages() {
    return ImmutableSet.of();
  }

  @Override
  public void generatePackages() {

  }

  @Override
  public V8ThreadLock<V8> v8Thread() {
    return MapNodesPlugin.get().bindings().v8Thread();
  }
}
