package net.year4000.mapnodes.messages;

import net.year4000.mapnodes.api.game.GamePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Msg {
    /** Load a util message */
    public static String util(String key, Object... args) {
        return String.format(MessageManager.get().getLocale("util").getProperty(key), args);
    }

    /** Load a message for the player's locale */
    public static String locale(Player player, String key, Object... args) {
        return String.format(MessageManager.get().getLocale(player.getLocale()).getProperty(key), args);
    }

    /** Load a message for the player's locale */
    public static String locale(GamePlayer player, String key, Object... args) {
        return String.format(MessageManager.get().getLocale(player.getPlayer().getLocale()).getProperty(key), args);
    }

    /** Load a message for the sender's locale */
    public static String locale(CommandSender sender, String key, Object... args) {
        if (sender instanceof Player) {
            return locale((Player)sender, key, args);
        }
        else {
            return util(key, args);
        }
    }
}
