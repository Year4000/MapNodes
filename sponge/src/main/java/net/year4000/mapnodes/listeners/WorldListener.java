/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes.listeners;

import com.google.inject.Inject;
import net.year4000.mapnodes.SpongeBindings;
import org.slf4j.Logger;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.entity.ConstructEntityEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.statistic.ChangeStatisticEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.event.world.SaveWorldEvent;
import org.spongepowered.api.world.World;

/** Listeners that are for world events */
public class WorldListener {

  @Inject private SpongeBindings $;
  @Inject private Logger logger;

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
}
