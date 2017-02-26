/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes.events;

import net.year4000.mapnodes.MapNodesPlugin;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.impl.AbstractEvent;

/** The base events for MapNodes */
public class MapNodesEvent extends AbstractEvent {
  private final MapNodesPlugin plugin = MapNodesPlugin.get();

  /** Inject the MapNodes events */
  public MapNodesEvent() {
    plugin.injector().injectMembers(this);
  }

  /** The cause is always MapNodesPlugin */
  @Override
  public Cause getCause() {
    return Cause.of(NamedCause.owner(plugin));
  }
}
