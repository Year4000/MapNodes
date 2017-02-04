/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes.nodes;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.io.Files;
import com.google.inject.Inject;
import net.lingala.zip4j.core.ZipFile;
import net.year4000.mapnodes.MapNodes;
import net.year4000.mapnodes.MapNodesPlugin;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.GameState;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldArchetypes;

import java.io.File;

public class SpongeNode extends Node {
  private World world;

  @Inject private Logger logger;
  @Inject private Game game;
  @Inject private EventManager eventManager;

  public SpongeNode(SpongeNodeFactory factory, MapPackage map) throws Exception {
    super(factory, map);
    MapNodesPlugin.get().inject(this);
  }

  @Override
  public void load() throws Exception {
    String zipLocation = MapNodes.SETTINGS.cache + "/" + id() + ".zip";
    File zipLocationFile = new File(zipLocation);
    Files.createParentDirs(zipLocationFile);
    Files.write(map.world().array(), zipLocationFile);
    ZipFile zip = new ZipFile(zipLocation);
    zip.extractAll("world/mapnodes-" + id());
    game.getServer().createWorldProperties("mapnodes-" + id(), WorldArchetypes.THE_VOID);
    // Trigger the world to be loaded once the server has been started
    if (game.getState() == GameState.SERVER_ABOUT_TO_START) {
      eventManager.registerListener(MapNodesPlugin.get(), GameStartedServerEvent.class, event -> {
        logger.info("Loading the world from event");
        loadWorld("mapnodes-" + id());
      });
    } else {
      logger.info("Loading the world at will");
      loadWorld("mapnodes-" + id());
    }
  }

  /** Load the world*/
  private void loadWorld(String id) {
    try {
      game.getServer().loadWorld(id).ifPresent(world -> this.world = world);
    } catch (Exception error) {
      logger.error("Fail to load world");
      try {
        unload();
      } catch (Exception exception) {
        logger.error("Fail to unload world, enforcing runtime is released");
      }
    }
  }

  @Override
  public void unload() throws Exception {
    // todo remove world from system
    super.unload();
  }

  /** Create the world transformer to spawn the player into the map */
  public Transform<World> worldTransformer() {
    return new Transform<>(world, new Vector3d(0, 64, 0));
  }
}
