/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes.nodes;

import com.google.inject.Inject;
import net.year4000.mapnodes.events.DeleteWorldEvent;
import org.slf4j.Logger;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.world.World;

/** This handles all the game logic to and from the v8 engine */
public class GameManager {

  @Inject private SpongeNode node;
  @Inject private Logger logger;
  @Inject private EventManager eventManager;
  @Inject private NodeManager nodeManager;

  /** This will cycle to the next game */
  public void cycle() throws Exception {
    SpongeNode lastNode = (SpongeNode) nodeManager.getNode();
    SpongeNode node = (SpongeNode) nodeManager.loadNextNode();
    logger.info("Cycling to Map " + node.name() + " version " + node.version());
    lastNode.world().ifPresent(world -> { // World never loaded
      world.getPlayers().forEach(player -> {
        Transform<World> transform = node.worldTransformer();
        player.transferToWorld(transform.getExtent(), transform.getPosition());
      });
      eventManager.post(new DeleteWorldEvent(lastNode));
    }).ifEmpty(() -> eventManager.post(new DeleteWorldEvent(lastNode)));
  }

  /** Get the internal id of from this GameManager */
  public int id() {
    return node.id();
  }
}
