/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes;

import com.flowpowered.math.vector.Vector3d;
import com.google.inject.Inject;
import net.year4000.mapnodes.nodes.NodeFactory;
import net.year4000.mapnodes.nodes.SpongeNode;
import net.year4000.mapnodes.nodes.SpongeNodeFactory;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.state.*;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.network.status.Favicon;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/** Sponge plugin to provide support for the MapNodes system */
@Plugin(id = "mapnodes", name = "MapNodes", version = "3.0.0-SNAPSHOT", dependencies = {@Dependency(id = "utilities")})
public class MapNodesPlugin implements MapNodes {
  private final SpongeBindings bindings = new SpongeBindings();
  private final SpongeNodeFactory nodeFactory = new SpongeNodeFactory();
  private static MapNodesPlugin inst;

  /** The game instance injected for mapnodes */
  @Inject
  private Game game;

  /** The logger injected from Sponge */
  @Inject
  private Logger logger;

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
    if (currentNode() == null) {
      String message = "No maps loaded!";
      logger().warn(message);
      game.getServer().shutdown(Text.of(TextColors.RED, message));
    }
  }

  @Listener
  public void onUnload(GameStoppingEvent event) {
    unload();
  }

  @Listener
  public void onClientPing(ClientPingServerEvent event) throws IOException {
    Favicon favicon = game.getRegistry().loadFavicon(new ByteArrayInputStream(currentNode().map().image().array()));
    event.getResponse().setFavicon(favicon);
    event.getResponse().setDescription(Text.of(currentNode().name() + " version " + currentNode().version()));
  }
}
