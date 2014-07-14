package net.year4000.mapnodes.messages;

import com.ewized.utilities.core.util.locale.LocaleManager;
import net.year4000.mapnodes.MapNodesPlugin;

public class MessageManager extends LocaleManager {
    private static MessageManager inst = null;
    private static final String LOCALE_PATH = "/locale/";

    private MessageManager(Class clazz, String path) {
        super(clazz, path);
        inst = this;

        // Load locales so we can have fun!
        for (String locale : new String[] {"messages", "en_US", "en_PT", "pt_PT", "pt_BR"}) {
            loadLocale(locale, clazz.getResourceAsStream(LOCALE_PATH + locale + ".properties"));
        }
        loadLocale(Msg.GIT, clazz.getResourceAsStream("/" + Msg.GIT + ".properties"));
    }

    public static MessageManager get() {
        if (inst == null) {
            inst = new MessageManager(MapNodesPlugin.class, LOCALE_PATH);
        }
        return inst;
    }
}
