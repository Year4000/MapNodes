/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes;

import com.google.inject.AbstractModule;
import net.year4000.mapnodes.nodes.NodeFactory;
import net.year4000.mapnodes.nodes.NodeManager;
import net.year4000.mapnodes.nodes.SpongeNodeFactory;
import net.year4000.utilities.Conditions;

/** The MapNodes module to inject Objects into the class */
public class MapNodesModule extends AbstractModule {
  private final MapNodesPlugin plugin;

  public MapNodesModule(MapNodesPlugin plugin) {
    this.plugin = Conditions.nonNull(plugin, "plugin");
  }

  @Override
  protected void configure() {
    bind(Settings.class).toInstance(MapNodes.SETTINGS);
    bind(NodeManager.class).toInstance(MapNodes.NODE_MANAGER);
    SpongeBindings bindings = new SpongeBindings();
    bind(Bindings.class).toInstance(bindings);
    bind(NodeFactory.class).toInstance(new SpongeNodeFactory());
    bind(MapNodes.class).toInstance(plugin);
  }
}
