/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes;

import com.google.inject.Inject;
import net.year4000.utilities.reflection.Gateways;
import net.year4000.utilities.reflection.Reflections;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.UUID;
import java.util.function.Consumer;

/** Bindings for Sponge */
public final class SpongeBindings extends Bindings {
  public final SpongeV8Bindings js = Reflections.proxy(SpongeV8Bindings.class, handler, Gateways.reflectiveImplements(SpongeV8Bindings.class));

  @Inject Logger logger;

  /** $.bindings.send_message */
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

  /** $.bindings.print */
  @Override
  @Bind
  public void print(String message) {
    logger.info(message.replaceAll("\n", ""));
  }

  /** Translate the Sponge to the V8Bindings interface */
  public interface SpongeV8Bindings extends V8Bindings {

  }
}
