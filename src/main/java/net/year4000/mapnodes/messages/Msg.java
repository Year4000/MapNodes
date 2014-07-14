package net.year4000.mapnodes.messages;

import net.year4000.mapnodes.api.game.GamePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Properties;

public class Msg {
    public static final String DEFAULT_LOCALE = "en_US";
    public static final String UTIL = "messages";
    public static final String GIT = "git";

    /** Load the git log key */
    public static String git(String key) {
        Properties locale = MessageManager.get() == null ? null : MessageManager.get().getLocale(GIT);
        return locale == null ? key : locale.getProperty(key, key);
    }

    /** Load a util message */
    public static String util(String key) {
        Properties locale = MessageManager.get() == null ? null : MessageManager.get().getLocale(UTIL);
        return locale == null ? key : locale.getProperty(key, key);
    }

    /** Load the message based off the locale string code */
    public static String locale(String locale, String key) {
        if (MessageManager.get().isLocale(locale)) {
            return MessageManager.get().getLocale(locale).getProperty(key, key);
        }
        else {
            Properties defaultLocale = MessageManager.get() == null ? null : MessageManager.get().getLocale(DEFAULT_LOCALE);
            return defaultLocale == null ? key : defaultLocale.getProperty(key, key);
        }
    }

    /** Load a message for the player's locale */
    public static String locale(Player player, String key) {
        return locale(player.getLocale(), key);
    }

    /** Load a message for the player's locale */
    public static String locale(GamePlayer player, String key) {
        return locale(player.getPlayer(), key);
    }

    /** Load a message for the sender's locale */
    public static String locale(CommandSender sender, String key) {
        if (sender instanceof Player) {
            return locale((Player)sender, key);
        }
        else {
            return locale(DEFAULT_LOCALE, key);
        }
    }
}
