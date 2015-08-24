/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.games;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.year4000.utilities.configs.Config;
import net.year4000.utilities.configs.ConfigURL;
import net.year4000.utilities.configs.JsonConfig;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ConfigURL(value = "https://api.year4000.net/configs/builders", config = Themes.class)
public class Themes extends JsonConfig {
    private static final Locale DEFAULT_KEY = Locale.US;

    /** The themes that are for this game mode */
    private List<Map<Locale, String>> themes = Lists.newArrayList();

    /** Get the instance of this theme */
    public static Themes get() {
        return JsonConfig.getInstance(new Themes());
    }

    /** Get a random theme */
    public Map<Locale, String> randomTheme() {
        Collections.shuffle(themes);
        Map<Locale, String> localeMap = Maps.newHashMap();
        // Fixes a bug, and force locales to be lowercase
        themes.get(0).forEach((key, value) -> localeMap.put(new Locale(key.toString()), value));
        return localeMap;
    }

    /** Get the translation of the theme */
    public String translateTheme(Map<Locale, String> theme, Locale locale) {
        checkNotNull(theme, "theme");
        checkNotNull(locale, "locale");

        if (theme.containsKey(locale)) {
            return theme.get(locale);
        }
        else if (theme.containsKey(DEFAULT_KEY)) {
            return theme.get(DEFAULT_KEY);
        }
        else {
            return theme.get(theme.keySet().iterator().next());
        }
    }
}
