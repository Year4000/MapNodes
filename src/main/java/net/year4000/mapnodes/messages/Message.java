package net.year4000.mapnodes.messages;

import net.year4000.utilities.bukkit.BukkitLocale;

public class Message extends BukkitLocale {
    public Message(String code) {
        super(null);
        locale = code;
        localeManager = MessageManager.get();
    }
}
