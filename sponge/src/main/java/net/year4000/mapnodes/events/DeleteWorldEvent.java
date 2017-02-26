/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes.events;

import net.year4000.mapnodes.nodes.SpongeNode;
import net.year4000.utilities.Conditions;
import org.spongepowered.api.world.World;

/** Called to delete the world */
public class DeleteWorldEvent extends MapNodesEvent {
  private final SpongeNode node;

  public DeleteWorldEvent(SpongeNode node) {
    this.node = Conditions.nonNull(node, "node");
  }

  /** The sponge node that we want to delete the world from */
  public SpongeNode spongeNode() {
    return node;
  }

  /** Get the world from the node to delete */
  public World world() {
    return node.worldTransformer().getExtent();
  }
}
