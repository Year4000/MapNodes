package net.year4000.mapnodes.messages;

import net.year4000.mapnodes.api.game.GamePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Properties;

public class Msg {
    //public static final String DEFAULT_LOCALE = Message.DEFAULT_LOCALE;
    //public static final String UTIL = "messages";
    //public static final String GIT = "git";

    /** Load the git log key */
    public static String git(String key) {
        //Properties locale = MessageManager.get() == null ? null : MessageManager.get().getLocale(GIT);
        //return locale == null ? key : locale.getProperty(key, key);
        return key;
    }

    /** Load a util message */
    public static String util(String key) {
        //Properties locale = MessageManager.get() == null ? null : MessageManager.get().getLocale(UTIL);
        //return locale == null ? key : locale.getProperty(key, key);
        return key;
    }

    /** Load the message based off the locale string code */
    public static String locale(String locale, String key, String... values) {
        return new Message(locale).get(key, values);
    }

    /** Load a message for the player's locale */
    public static String locale(Player player, String key, String... values) {
        return locale(player.getLocale(), key, values);
    }

    /** Load a message for the player's locale */
    public static String locale(GamePlayer player, String key, String... values) {
        return locale(player.getPlayer(), key, values);
    }

    /** Load a message for the sender's locale */
    public static String locale(CommandSender sender, String key, String... values) {
        if (sender instanceof Player) {
            return locale((Player)sender, key, values);
        }
        else {
            return locale(Message.DEFAULT_LOCALE, key, values);
        }
    }
}
