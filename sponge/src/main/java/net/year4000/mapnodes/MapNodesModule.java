/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import net.year4000.mapnodes.nodes.NodeFactory;
import net.year4000.mapnodes.nodes.NodeManager;
import net.year4000.mapnodes.nodes.SpongeNodeFactory;
import net.year4000.utilities.Conditions;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.SpongeExecutorService;

/** The MapNodes module to inject Objects into the class */
public class MapNodesModule extends AbstractModule {
  private final MapNodesPlugin plugin;

  public MapNodesModule(MapNodesPlugin plugin) {
    this.plugin = Conditions.nonNull(plugin, "plugin");
  }

  @Override
  protected void configure() {
    bind(MapNodes.class).toInstance(plugin);
    bind(Settings.class).toInstance(MapNodes.SETTINGS);
    bind(NodeManager.class).toInstance(MapNodes.NODE_MANAGER);
    bind(Bindings.class).to(SpongeBindings.class).asEagerSingleton();
    bind(NodeFactory.class).to(SpongeNodeFactory.class).asEagerSingleton();
    bind(SpongeExecutorService.class).annotatedWith(Names.named("async")).toInstance(Sponge.getScheduler().createAsyncExecutor(plugin));
    bind(SpongeExecutorService.class).toInstance(Sponge.getScheduler().createSyncExecutor(plugin));
  }
}
