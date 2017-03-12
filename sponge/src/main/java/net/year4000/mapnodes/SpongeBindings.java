/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import com.google.common.base.CaseFormat;
import com.google.inject.Inject;
import net.year4000.utilities.Utils;
import net.year4000.utilities.reflection.Gateways;
import net.year4000.utilities.reflection.Reflections;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.text.Text;

import java.util.UUID;
import java.util.function.Consumer;

/** Bindings for Sponge */
public final class SpongeBindings extends Bindings {
  public final SpongeV8Bindings js = Reflections.proxy(SpongeV8Bindings.class, handler, Gateways.reflectiveImplements(SpongeV8Bindings.class));

  @Inject private Game game;

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

  /** $.bindings.player_meta_uuid */
  @Override
  @Bind
  public V8Object playerMetaUuid(String uuid) {
    try (V8ThreadLock<V8> lock = v8Thread()) {
      Player player = game.getServer().getPlayer(UUID.fromString(uuid)).orElse(null);
      return new V8Object(lock.v8())
        .add("uuid", player.getUniqueId().toString())
        .add("username", player.getName());
    }
  }

  /** Translate the Sponge to the V8Bindings interface */
  public interface SpongeV8Bindings extends V8Bindings {

    /** $.js.on_event */
    default void onEvent(Event event) {
      String className = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, event.getClass().getSimpleName());
      onEvent("on_" + className.replaceAll("(\\$_impl|\\$)", ""), Utils.toString(event.getCause().all()));
    }

    /** $.js.join_game */
    default void joinGame(Player player) {
      joinGame(player.getUniqueId().toString());
    }
  }
}
