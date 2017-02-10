/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes;

import net.year4000.utilities.reflection.Gateways;
import net.year4000.utilities.reflection.Reflections;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.UUID;
import java.util.function.Consumer;

/** Bindings for Sponge */
public final class SpongeBindings extends Bindings {

  /** Translate the Sponge to the V8Bindings interface */
  interface SpongeV8Bindings extends V8Bindings {

  }

  /** The class type of the child bindings */
  protected SpongeV8Bindings bindings() {
    return Reflections.proxy(SpongeV8Bindings.class, handler, Gateways.reflectiveImplements(SpongeV8Bindings.class));
  }

  @Override
  @Bind
  public void sendMessage(String player, String message) {
    Consumer<Player> consumer = value -> value.sendMessage(Text.of(message));
    try {
      UUID uuid = UUID.fromString(player);
      Sponge.getServer().getPlayer(uuid).ifPresent(consumer);
    } catch (IllegalArgumentException error) {
      Sponge.getServer().getPlayer(player).ifPresent(consumer);
    }
  }
}
