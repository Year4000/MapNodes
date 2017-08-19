/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes;

import com.eclipsesource.v8.V8Array;
import com.google.common.base.CaseFormat;
import com.google.inject.Inject;
import net.year4000.utilities.reflection.Gateways;
import net.year4000.utilities.reflection.Reflections;
import net.year4000.utilities.value.Value;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.UUID;
import java.util.function.Consumer;

/** Bindings for Sponge */
public final class SpongeBindings extends Bindings {
  public final SpongeV8Bindings js = Reflections.proxy(SpongeV8Bindings.class, handler, Gateways.reflectiveImplements(SpongeV8Bindings.class));

  @Inject private Game game;
  @Inject private Logger logger;

  /** Internal method to get the player from the uuid or user name*/
  private Value<Player> player(String player) {
    if (player.length() > 16) {
      return Value.of(game.getServer().getPlayer(UUID.fromString(player)));
    } else {
      return Value.of(game.getServer().getPlayer(player));
    }
  }

  /** $.bindings.send_locale_message */
  @Bind
  public void sendLocaleMessage(String player, String name, V8Array array) {
    player(player).ifPresent(value -> {
      try {
        // todo support messages that are not in the enum
        value.sendMessage(Messages.valueOf(name).get(value, Commons.toObjectArray(array)));
      } catch (IllegalArgumentException error) {
        logger.error("Locale for {} is not found in Messages.class", name);
      }
    });
  }

  /** $.bindings.get_locale_message */
  @Bind
  public String getLocaleMessage(String player, String name, V8Array array) {
    Value<Player> playerValue = player(player);
    if (playerValue.isPresent()) {
      try {
        // todo support messages that are not in the enum
        return TextSerializers.FORMATTING_CODE.serializeSingle(Messages.valueOf(name).get(playerValue.get(), Commons.toObjectArray(array)));
      } catch (IllegalArgumentException error) {
        logger.error("Locale for {} is not found in Messages.class", name);
        return "null";
      }
    } else {
      return "null";
    }
  }

  /** $.bindings.send_message */
  @Override
  @Bind
  public void sendMessage(String player, String message) {
    Text deserialize = TextSerializers.FORMATTING_CODE.deserialize(message);
    player(player).ifPresent(value -> value.sendMessage(Text.of(deserialize)));
  }

  /** $.bindings.player_meta_uuid */
  @Override
  @Bind
  public String playerMetaUuid(String uuid) {
    Player player = player(uuid).get();
    return player.getName() + ":" + uuid;
  }

  /** $.bindings.tablist_header */
  @Bind
  public void tablistHeader(String uuid, String header) {
    Text deserialize = TextSerializers.FORMATTING_CODE.deserialize(header);
    Consumer<Player> setHeader = player -> player.getTabList().setHeader(deserialize);
    player(uuid).ifPresent(setHeader).ifEmpty(() -> game.getServer().getOnlinePlayers().forEach(setHeader));
  }

  /** $.bindings.tablist_footer */
  @Bind
  public void tablistFooter(String uuid, String footer) {
    Text deserialize = TextSerializers.FORMATTING_CODE.deserialize(footer);
    Consumer<Player> setFooter = player -> player.getTabList().setFooter(deserialize);
    player(uuid).ifPresent(setFooter).ifEmpty(() -> game.getServer().getOnlinePlayers().forEach(setFooter));
  }

  /** $.bindings.has_permission */
  @Bind
  public boolean hasPermission(String uuid, String permission) {
    Value<Player> player = player(uuid);
    return player.isPresent() && player.get().hasPermission(permission);
  }

  /** $.bindings.teleport */
  @Bind
  public void teleport(String uuid, double x, double y, double z) {
    player(uuid).ifPresent(player -> player.transferToWorld(player.getWorld(), Commons.center(x, y, z)));
  }

  /** Translate the Sponge to the V8Bindings interface */
  public interface SpongeV8Bindings extends V8Bindings {

    /** $.js.on_event */
    default void onEvent(Event event) {
      String className = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, event.getClass().getSimpleName());
      onEvent("on_" + className.replaceAll("(\\$_impl|\\$)", ""), className);
    }

    /** $.js.join_game */
    default void joinGame(Player player) {
      joinGame(player.getUniqueId().toString());
    }

    /** $.js.leave_game */
    default void leaveGame(Player player) {
      leaveGame(player.getUniqueId().toString());
    }

    /** $.js.command_description */
    default String commandDescription(String command) {
      return "No description is present yet"; // todo allow getting the description from command
    }

    /** $.js.command_permission */
    default String commandPermission(String command) {
      return null; // todo allow getting permissions from command
    }
  }
}
