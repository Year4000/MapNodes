/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Injector;
import net.year4000.mapnodes.listeners.GameListener;
import net.year4000.mapnodes.listeners.WorldListener;
import net.year4000.mapnodes.nodes.NodeFactory;
import net.year4000.mapnodes.nodes.SpongeNode;
import net.year4000.mapnodes.nodes.SpongeNodeFactory;
import net.year4000.utilities.Conditions;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.*;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

/** Sponge plugin to provide support for the MapNodes system */
@Plugin(id = "mapnodes", name = "MapNodes", version = "3.0.0-SNAPSHOT", dependencies = {@Dependency(id = "utilities")})
public class MapNodesPlugin implements MapNodes {
  private static MapNodesPlugin inst;
  private Injector mapNodesInjector;

  /** The game instance injected for mapnodes */
  @Inject private Game game;
  /** The logger injected from Sponge */
  @Inject private Logger logger;
  /** The injector injected from Sponge */
  @Inject private Injector injector;
  /** The event manager from sponge */
  @Inject private EventManager eventManager;

  /** Get the instance of this plugin */
  public static MapNodesPlugin get() {
    if (inst == null) {
      Sponge.getPluginManager().getPlugin("mapnodes")
        .ifPresent(container -> container.getInstance().ifPresent(obj -> inst = (MapNodesPlugin) obj));
    }
    return inst;
  }

  @Override
  public Logger logger() {
    return logger;
  }

  @Override
  public SpongeBindings bindings() {
    return (SpongeBindings) mapNodesInjector.getInstance(Bindings.class);
  }

  @Override
  public SpongeNodeFactory nodeFactory() {
    return (SpongeNodeFactory) mapNodesInjector.getInstance(NodeFactory.class);
  }

  @Override
  public SpongeNode currentNode() {
    return (SpongeNode) MapNodes.super.currentNode();
  }

  /** Get the injector for MapNodes its a child injector from Sponge */
  public Injector injector() {
    return Conditions.nonNull(mapNodesInjector, "mapNodesInjector");
  }

  // Events for MapNodes

  @Listener
  public void onLoad(GameConstructionEvent event) {
    logger().info("Fetching locales for cache");
    Messages.Factory.inst.get();
    logger().info("Creating child injector for MapNodes");
    Conditions.nonNull(injector, "injector");
    mapNodesInjector = injector.createChildInjector(new MapNodesModule(this));
    load();
    bindings().js.load();
  }

  @Listener
  public void onEnable(GameAboutToStartServerEvent event) {
    enable();
    if (currentNode() == null) {
      String message = "No maps loaded!";
      logger().warn(message);
      game.getServer().shutdown(Text.of(TextColors.RED, message));
    }
    // Register listeners
    ImmutableList.of(GameListener.class, WorldListener.class).forEach(clazz -> {
      logger().info("Injecting and registering listener for: " + clazz.getSimpleName());
      eventManager.registerListeners(MapNodesPlugin.this, mapNodesInjector.getInstance(clazz));
    });
  }

  @Listener
  public void onUnload(GameStoppingEvent event) {
    unload();
  }
}
