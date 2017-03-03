/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes;

import com.google.inject.Inject;
import net.year4000.mapnodes.nodes.SpongeNode;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.*;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.world.World;

import static net.year4000.mapnodes.MapNodes.NODE_MANAGER;

/** Sponge plugin to provide support for the MapNodes system */
@Plugin(id = "mapnodes_debug", name = "MapNodesDebug", dependencies = {@Dependency(id = "mapnodes")})
public class MapNodesDebugPlugin {

  /** The logger injected from Sponge */
  @Inject private Logger logger;

  @Listener
  public void onEnable(GameAboutToStartServerEvent event) {
    // debug while creating the core system
    Sponge.getCommandManager().register(this, CommandSpec.builder().executor((src, args) -> {
      try {
        SpongeNode node = (SpongeNode) NODE_MANAGER.getNode();
        node.gameManager().cycle();
      } catch (Exception e) {
        e.printStackTrace();
      }
      return CommandResult.success();
    }).build(), "next");

    // debug while creating the core system
    Sponge.getCommandManager().register(this, CommandSpec.builder().executor((src, args) -> {
      try {
        SpongeNode node = (SpongeNode) NODE_MANAGER.getNode();
        node.gameManager().start();
      } catch (Exception e) {
        e.printStackTrace();
      }
      return CommandResult.success();
    }).build(), "start");
  }

}
