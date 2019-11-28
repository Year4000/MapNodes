/*
 * Copyright 2019 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes.nodes;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.year4000.mapnodes.SpongeBindings;
import net.year4000.mapnodes.events.DeleteWorldEvent;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.entity.ConstructEntityEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.statistic.ChangeStatisticEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.event.world.SaveWorldEvent;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.world.World;

import java.util.concurrent.TimeUnit;

/** The global game manager that handles events that are global and not related to the node */
public class GlobalGameManager {
  @Inject private Game game;
  @Inject private Logger logger;
  @Inject private EventManager eventManager;
  @Inject private SpongeBindings $;
  @Inject @Named("async") private SpongeExecutorService executorService;

  /** Do not modify the world when the game is not running */
  @Listener(order = Order.FIRST)
  public void on(ChangeBlockEvent event) {
    if (!$.js.isGameRunning()) {
      if (logger.isDebugEnabled()) {
        logger.debug("Game is not running canceling event: " + event.getClass().getSimpleName());
      }
      event.setCancelled(true);
    }
  }

  /** Do not damage entities when game is not running */
  @Listener(order = Order.FIRST)
  public void on(DamageEntityEvent event) {
    if (!$.js.isGameRunning()) {
      if (logger.isDebugEnabled()) {
        logger.debug("Game is not running canceling event: " + event.getClass().getSimpleName());
      }
      event.setCancelled(true);
    }
  }

  /** Do not spawn entities game is not running */
  @Listener(order = Order.FIRST)
  public void on(ConstructEntityEvent.Pre event) {
    if (!$.js.isGameRunning()) {
      if (logger.isDebugEnabled()) {
        logger.debug("Game is not running canceling event: " + event.getClass().getSimpleName());
      }
      event.setCancelled(true);
    }
  }

  /** There is no need to save the world to disk */
  @Listener(order = Order.FIRST)
  public void on(SaveWorldEvent event) {
    event.setCancelled(true);
  }

  /** There is no need to save the player stats */
  @Listener(order = Order.FIRST)
  public void on(ChangeStatisticEvent event) {
    event.setCancelled(true);
  }

  /** Only load MapNodes worlds and disable spawn chunks */
  @Listener(order = Order.FIRST)
  public void on(LoadWorldEvent event, @Getter("getTargetWorld") World world) {
    world.getProperties().setGenerateSpawnOnLoad(false);
    if (!world.getName().startsWith("mapnodes")) {
      if (logger.isDebugEnabled()) {
        logger.debug("Not loading this world: " + world.getName());
      }
      event.setCancelled(true);
    }
  }

  /** Delete the world from the node at command */
  @Listener
  public void on(DeleteWorldEvent event, @Getter("world") World world, @Getter("spongeNode") SpongeNode node) {
    if (!world.isLoaded() || game.getServer().unloadWorld(world)) {
      game.getServer().deleteWorld(world.getProperties()).thenRun(() -> {
        logger.info("World was deleted: mapnodes-" + node.id());
      });
    } else if (game.getServer().getWorlds().contains(world)) {
      logger.warn("Could not unload world (retry in 5s): mapnodes-" + node.id());
      world.getPlayers().forEach(Player::kick); // No players should be on
      executorService.schedule(() -> eventManager.post(event), 5, TimeUnit.SECONDS);
    } else {
      logger.info("World was deleted: mapnodes-" + node.id());
    }
  }
}
