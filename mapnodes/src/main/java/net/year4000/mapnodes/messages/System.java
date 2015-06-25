/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.messages;

import net.year4000.utilities.bukkit.BukkitLocale;

public class System extends BukkitLocale {
    public System() {
        super(null);
        locale = DEFAULT_LOCALE;
        localeManager = SystemManager.get();
    }
}
