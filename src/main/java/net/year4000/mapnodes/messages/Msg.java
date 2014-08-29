package net.year4000.mapnodes.messages;

import net.year4000.mapnodes.api.game.GamePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Msg {
    private static System util = new System();

    /** Load the git log key */
    public static String git(String key) {
        return Git.get(key);
    }

    /** Load a util message */
    public static String util(String key, String... values) {
        return util.get(key, values);
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
