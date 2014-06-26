package net.year4000.mapnodes.messages;

import com.ewized.utilities.core.util.locale.LocaleManager;
import net.year4000.mapnodes.MapNodesPlugin;

public class MessageManager extends LocaleManager {
    private static MessageManager inst = null;
    private static final String LOCALE_PATH = "/locales/";

    private MessageManager(Class clazz, String path) {
        super(clazz, path);
        inst = this;

        // Plugin util messages for internal use only
        loadLocale("util", clazz.getResourceAsStream(LOCALE_PATH + "util.properties"));
    }

    public static MessageManager get() {
        if (inst == null) {
            inst = new MessageManager(MapNodesPlugin.class, LOCALE_PATH);
        }
        return inst;
    }
}
