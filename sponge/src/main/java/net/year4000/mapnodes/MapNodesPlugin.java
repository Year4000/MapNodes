/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes;

import net.year4000.mapnodes.nodes.NodeFactory;
import net.year4000.mapnodes.nodes.SpongeNodeFactory;
import org.spongepowered.api.plugin.Plugin;

@Plugin(id = "mapnodes")
public class MapNodesPlugin {
  private final Bindings bindings = new SpongeBindings();
  private final NodeFactory nodeFactory = new SpongeNodeFactory();
}
