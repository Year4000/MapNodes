package net.year4000.mapnodes.messages;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Getter;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.utilities.bukkit.BukkitLocale;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public final class Msg {
    private static System util = new System();
    public static String NOTICE = MessageUtil.message(" &7[&e!&7] &e");
    @Getter
    private static LoadingCache<Player, String> codes = CacheBuilder.newBuilder()
        .expireAfterAccess(5, TimeUnit.MINUTES)
        .build(new CacheLoader<Player, String>() {
            @Override
            public String load(Player player) throws Exception {
                return player.getLocale() == null ? BukkitLocale.DEFAULT_LOCALE : player.getLocale();
            }
        });

    private Msg() {/* Util Class */}

    /** Load the git log key */
    public static String git(String key) {
        return Git.get(key);
    }

    /** Load a util message */
    public static String util(String key, String... values) {
        return util.get(key, values);
    }

    /** Load the message based off the locale string code */
    @Deprecated
    public static String locale(String locale, String key, String... values) {
        return new Message(locale).get(key, values);
    }

    /** Load a message for the player's locale */
    public static String locale(Player player, String key, String... values) {
        return locale(codes.getUnchecked(player), key, values);
    }

    /** Load a message for the player's locale */
    public static String locale(GamePlayer player, String key, String... values) {
        return locale(codes.getUnchecked(player.getPlayer()), key, values);
    }

    /** Load a message for the sender's locale */
    public static String locale(CommandSender sender, String key, String... values) {
        if (sender instanceof Player) {
            return locale(codes.getUnchecked((Player) sender), key, values);
        }
        else {
            return locale(Message.DEFAULT_LOCALE, key, values);
        }
    }

    /** Does the string match the locale or locale key */
    public static boolean matches(String locale, String compare, String key) {
        return compare.equalsIgnoreCase(key) || compare.equalsIgnoreCase(locale(locale, key, compare));
    }

    /** Does the string match the locale or locale key */
    public static boolean matches(Player player, String compare, String key) {
        return matches(codes.getUnchecked(player), compare, key);
    }

    /** Does the string match the locale or locale key */
    public static boolean matches(GamePlayer player, String compare, String key) {
        return matches(codes.getUnchecked(player.getPlayer()), compare, key);
    }
}
