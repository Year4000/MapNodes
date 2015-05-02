package net.year4000.mapnodes.messages;

import net.year4000.mapnodes.Settings;
import net.year4000.utilities.cache.QuickCache;
import net.year4000.utilities.locale.URLLocaleManager;

public class SystemManager extends URLLocaleManager {
    private static QuickCache<SystemManager> inst = QuickCache.builder(SystemManager.class).build();
    private static String url = Settings.get().getSystemLocales();

    public SystemManager() {
        super(url, parseJson(url + LOCALES_JSON));
    }

    public static SystemManager get() {
        return inst.get();
    }
}
