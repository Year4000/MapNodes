/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes.nodes;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;

public class SpongeNodeFactory implements NodeFactory {
  @Override
  public Node create(MapPackage map) {
    return new SpongeNode();
  }

  @Override
  public Collection<MapPackage> packages() {
    return ImmutableSet.of();
  }

  @Override
  public void generatePackages() {

  }
}
