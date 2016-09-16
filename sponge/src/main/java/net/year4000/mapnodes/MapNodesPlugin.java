/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes;

import com.google.inject.Inject;
import net.year4000.mapnodes.nodes.NodeFactory;
import net.year4000.mapnodes.nodes.SpongeNodeFactory;
import org.slf4j.Logger;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameLoadCompleteEvent;
import org.spongepowered.api.event.game.state.GameStoppingEvent;
import org.spongepowered.api.plugin.Plugin;

/** Sponge plugin to provide support for the MapNodes system */
@Plugin(id = "mapnodes", name = "MapNodes", version = "3.0.0-SNAPSHOT")
public class MapNodesPlugin implements MapNodes {
  private final SpongeBindings bindings = new SpongeBindings();
  private final SpongeNodeFactory nodeFactory = new SpongeNodeFactory();

  /** The logger injected from Sponge */
  @Inject
  private Logger logger;

  @Override
  public Logger logger() {
    return logger;
  }

  @Override
  public Bindings bindings() {
    return bindings;
  }

  @Override
  public NodeFactory nodeFactory() {
    return nodeFactory;
  }

  // Events for MapNodes

  @Listener
  public void onLoad(GameConstructionEvent event) {
    load();
  }

  @Listener
  public void onEnable(GameLoadCompleteEvent event) {
    enable();
  }

  @Listener
  public void onEnable(GameStoppingEvent event) {
    unload();
  }
}
