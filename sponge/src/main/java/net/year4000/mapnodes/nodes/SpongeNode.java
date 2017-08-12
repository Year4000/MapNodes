/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes.nodes;

import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.utils.MemoryManager;
import com.flowpowered.math.vector.Vector3d;
import com.google.common.io.Files;
import com.google.inject.Inject;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.year4000.mapnodes.MapNodes;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.SpongeBindings;
import net.year4000.utilities.value.Value;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.GameState;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.world.GeneratorTypes;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldArchetypes;
import org.spongepowered.api.world.gen.WorldGeneratorModifiers;

import java.io.File;
import java.util.Collections;

public class SpongeNode extends Node {
  private World world;

  @Inject private GameManager gameManager;
  @Inject private MapNodesPlugin plugin;
  @Inject private Logger logger;
  @Inject private Game game;
  @Inject private EventManager eventManager;
  @Inject private SpongeBindings $;

  @Inject
  public SpongeNode(SpongeNodeFactory factory, MapPackage map) throws Exception {
    super(factory, map);
  }

  @Override
  public void load() throws Exception {
    logger.info("Creating MemoryManager for current game");
    memoryManager = new MemoryManager(v8Object.getRuntime());
    String zipLocation = MapNodes.SETTINGS.cache + "/" + id() + ".zip";
    File zipLocationFile = new File(zipLocation);
    Files.createParentDirs(zipLocationFile);
    Files.write(map.world().array(), zipLocationFile);
    ZipFile zipFile = new ZipFile(zipLocation);
    try {
      zipFile.removeFile("level.dat"); // Sponge Really Does not like level.dat
    } catch (ZipException ignore) {
      logger.warn("level.dat was not found, this is actually good");
    }
    zipFile.extractAll("world/mapnodes-" + id());
    game.getServer().createWorldProperties("mapnodes-" + id(), WorldArchetypes.THE_VOID);
    // Trigger the world to be loaded once the server has been started
    if (game.getState() == GameState.SERVER_ABOUT_TO_START) {
      eventManager.registerListener(plugin, GameStartedServerEvent.class, event -> {
        logger.info("Loading the world from event");
        loadWorld("mapnodes-" + id());
      });
    } else {
      logger.info("Loading the world at will");
      loadWorld("mapnodes-" + id());
    }
    logger.info("Registering listeners for current node");
    eventManager.registerListeners(plugin, gameManager);
  }

  /** Load the world*/
  private void loadWorld(String name) {
    try {
      game.getServer().getWorldProperties(name).ifPresent(properties -> {
        properties.setEnabled(true);
        properties.setKeepSpawnLoaded(false);
        properties.setGenerateSpawnOnLoad(false);
        properties.setMapFeaturesEnabled(false);
        properties.setGeneratorType(GeneratorTypes.FLAT);
        properties.setGeneratorModifiers(Collections.singleton(WorldGeneratorModifiers.VOID));
        game.getServer().saveWorldProperties(properties);
      });
      game.getServer().loadWorld(name).ifPresent(world -> this.world = world);
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
    logger.info("Unregister listeners and releasing v8 bindings");
    $.releaseHandler();
    eventManager.unregisterListeners(gameManager);
    super.unload(); //  Handle the V8
  }

  /** Create the world transformer to spawn the player into the map */
  public Transform<World> worldTransformer() {
    V8Array array = $.js.spawnPoint();
    try {
      return new Transform<>(world, new Vector3d(array.getDouble(0), array.getDouble(1), array.getDouble(2)));
    } finally {
      array.release();
    }
  }

  /** Get the world of this node */
  public Value<World> world() {
    return Value.of(world);
  }

  /** Get the game manager for this node */
  public GameManager gameManager() {
    return gameManager;
  }
}
