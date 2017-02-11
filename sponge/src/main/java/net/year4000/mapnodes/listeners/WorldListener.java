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

/** Listeners that are for world events */
public class WorldListener {

  @Inject MapNodesPlugin mapnodes;
  @Inject SpongeBindings $;
  @Inject Game game;


  @Listener
  public void on(ChangeBlockEvent event) {
    if (!$.js.isGameRunning()) {
      mapnodes.logger().debug("Game is not running canceling event: " + event.getClass().getSimpleName());
      event.setCancelled(true);
    }
  }
}
