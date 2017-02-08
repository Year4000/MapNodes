/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes;

import com.google.inject.Inject;
import com.google.inject.Injector;
import net.year4000.mapnodes.nodes.Node;
import net.year4000.mapnodes.nodes.NodeFactory;
import net.year4000.mapnodes.nodes.SpongeNode;
import net.year4000.utilities.Conditions;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.*;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.network.status.Favicon;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

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
    return mapNodesInjector.getInstance(Bindings.class);
  }

  @Override
  public NodeFactory nodeFactory() {
    return mapNodesInjector.getInstance(NodeFactory.class);
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
  }

  @Listener
  public void onEnable(GameAboutToStartServerEvent event) {
    enable();
    if (currentNode() == null) {
      String message = "No maps loaded!";
      logger().warn(message);
      game.getServer().shutdown(Text.of(TextColors.RED, message));
    }
    Sponge.getCommandManager().register(this, CommandSpec.builder().executor((src, args) -> {
      try {
        Node node = NODE_MANAGER.loadNextNode();
        logger().info("Map " + node.name() + " version " + node.version());
      } catch (Exception e) {
        e.printStackTrace();
      }
      return CommandResult.success();
    }).build(), "next");
  }

  @Listener
  public void onUnload(GameStoppingEvent event) {
    unload();
  }

  @Listener
  public void onClientPing(ClientPingServerEvent event) throws IOException {
    if (currentNode() == null) return;
    // Resize to 64 x 64
    BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(currentNode().map().image().array()));
    BufferedImage bufferedIcon = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
    Graphics graphic = bufferedIcon.createGraphics();
    graphic.drawImage(bufferedImage, 0, 0, 64, 64, null);
    graphic.dispose();
    Favicon favicon = game.getRegistry().loadFavicon(bufferedIcon);
    event.getResponse().setFavicon(favicon);
    event.getResponse().setDescription(Text.of(currentNode().name() + " version " + currentNode().version()));
  }

  @Listener
  public void join(ClientConnectionEvent.Login event) {
    SpongeNode node = (SpongeNode) currentNode();
    event.setToTransform(node.worldTransformer());
  }

  @Listener
  public void join(ClientConnectionEvent.Join event) {
    event.getTargetEntity().gameMode().set(GameModes.SPECTATOR);
  }
}
