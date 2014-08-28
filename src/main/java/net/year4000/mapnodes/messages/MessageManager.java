package net.year4000.mapnodes.messages;

import net.year4000.utilities.locale.ClassLocaleManager;

public class MessageManager extends ClassLocaleManager {
    private static MessageManager inst = null;
    private static final String LOCALE_PATH = "/locale/";

    private MessageManager(String path, String... locales) {
        super(path, locales);
        inst = this;
    }

    public static MessageManager get() {
        if (inst == null) {
            inst = new MessageManager(LOCALE_PATH, "messages", "en_US", "en_PT", "pt_PT", "pt_BR");
        }
        return inst;
    }
}
