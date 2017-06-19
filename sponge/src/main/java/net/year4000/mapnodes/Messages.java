/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes;

import net.year4000.utilities.cache.QuickCache;
import net.year4000.utilities.locale.LocaleKeys;
import net.year4000.utilities.locale.Translatable;
import net.year4000.utilities.locale.URLLocaleManager;
import net.year4000.utilities.sponge.SpongeLocale;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import java.util.Optional;

/** All the messages needed for MapNodes */
public enum Messages implements LocaleKeys<CommandSource, Text> {
  // Locale Header
  LOCALE_CODE,
  LOCALE_NAME,

  CMD_MAPNODES_LOCALE_NAME,
  CMD_MAPNODES_LOCALE_CODE,

  TEAM_JOIN
  ;

  public static final Text SUCCESS = net.year4000.utilities.sponge.Messages.SUCCESS;
  public static final Text NOTICE = net.year4000.utilities.sponge.Messages.NOTICE;
  public static final Text ERROR = net.year4000.utilities.sponge.Messages.ERROR;

  @Override
  public Translatable<Text> apply(Optional<CommandSource> player) {
    if (player.isPresent()) {
      return new SpongeLocale(Factory.inst.get(), player.get());
    }

    return new SpongeLocale(Factory.inst.get());
  }

  /** The factory to handle Locale Managers */
  public static class Factory extends URLLocaleManager {
    static QuickCache<Messages.Factory> inst = QuickCache.builder(Messages.Factory.class).build();

    public Factory() {
      super("https://raw.githubusercontent.com/Year4000/Locales/master/mapnodes/");
    }
  }
}
