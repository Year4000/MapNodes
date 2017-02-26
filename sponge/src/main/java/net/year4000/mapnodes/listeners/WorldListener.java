/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes.listeners;

import com.google.inject.Inject;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.SpongeBindings;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.world.SaveWorldEvent;

/** Listeners that are for world events */
public class WorldListener {

  @Inject private MapNodesPlugin mapnodes;
  @Inject private SpongeBindings $;
  @Inject private Game game;

  /** Do not modify the world when the game is not running */
  @Listener
  public void on(ChangeBlockEvent event) {
    if (!$.js.isGameRunning()) {
      //mapnodes.logger().debug("Game is not running canceling event: " + event.getClass().getSimpleName());
      event.setCancelled(true);
    }
  }

  /** Do not damage entities when game is not running */
  @Listener
  public void on(DamageEntityEvent event) {
    if (!$.js.isGameRunning()) {
      //mapnodes.logger().debug("Game is not running canceling event: " + event.getClass().getSimpleName());
      event.setCancelled(true);
    }
  }

  /** There is no need to save the world to disk */
  @Listener
  public void on(SaveWorldEvent event) {
    event.setCancelled(true);
  }
}
