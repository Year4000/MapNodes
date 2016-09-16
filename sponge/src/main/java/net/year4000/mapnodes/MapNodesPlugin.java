/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes;

import net.year4000.mapnodes.nodes.NodeFactory;
import net.year4000.mapnodes.nodes.SpongeNodeFactory;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameLoadCompleteEvent;
import org.spongepowered.api.event.game.state.GameStoppingEvent;
import org.spongepowered.api.plugin.Plugin;

@Plugin(id = "mapnodes", name = "MapNodes", version = "3.0.0-SNAPSHOT")
public class MapNodesPlugin implements MapNodes {
  private final SpongeBindings bindings = new SpongeBindings();
  private final SpongeNodeFactory nodeFactory = new SpongeNodeFactory();

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
