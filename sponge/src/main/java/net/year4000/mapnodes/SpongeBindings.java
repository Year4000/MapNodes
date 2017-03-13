/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes;

import com.google.common.base.CaseFormat;
import com.google.inject.Inject;
import net.year4000.utilities.Utils;
import net.year4000.utilities.reflection.Gateways;
import net.year4000.utilities.reflection.Reflections;
import net.year4000.utilities.value.Value;
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

  /** Internal method to get the player from the uuid or user name*/
  private Value<Player> player(String player) {
    if (player.length() > 16) {
      return Value.of(game.getServer().getPlayer(UUID.fromString(player)));
    } else {
      return Value.of(game.getServer().getPlayer(player));
    }
  }

  /** $.bindings.send_message */
  @Override
  @Bind
  public void sendMessage(String player, String message) {
    player(player).ifPresent(value -> value.sendMessage(Text.of(message)));
  }

  /** $.bindings.player_meta_uuid */
  @Override
  @Bind
  public String playerMetaUuid(String uuid) {
    Player player = player(uuid).get();
    return player.getName() + ":" + uuid;
  }

  /** $.bindings.teleport */
  public void teleport(String uuid, int x, int y, int z) {
    player(uuid).ifPresent(player -> player.transferToWorld(player.getWorld(), Commons.center(x, y, z)));
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

    /** $.js.leave_game */
    default void leaveGame(Player player) {
      leaveGame(player.getUniqueId().toString());
    }
  }
}
