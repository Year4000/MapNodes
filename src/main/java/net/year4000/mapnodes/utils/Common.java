package net.year4000.mapnodes.utils;

import com.google.common.base.Ascii;
import lombok.Synchronized;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.clocks.Clocker;
import net.year4000.mapnodes.game.NodePlayer;
import net.year4000.mapnodes.game.regions.types.Point;
import net.year4000.utilities.ChatColor;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public final class Common {
    public static final Random rand = new Random();

    private Common() {
        // Utility Class
    }

    /** Color numbers based on its percent */
    private static int toggle = 0;

    public static String colorNumber(int current, int total) {
        return MessageUtil.replaceColors(chatColorNumber(current, total) + current);
    }

    public static String chatColorNumber(int current, int total) {
        double percent = ((double)current / (double)total) * 100;
        ChatColor color;

        //LogUtil.log(percent+"");

        // danger dark red
        if (percent < 10) {
            toggle++;
            color = toggle % 10 == 0 ? ChatColor.DARK_RED : ChatColor.RED;
        }
        // danger red
        else if (percent < 20) {
            color = ChatColor.RED;
        }
        // warning yellow
        else if (percent < 50) {
            color = ChatColor.YELLOW;
        }
        // good green
        else {
            color = ChatColor.GREEN;
        }

        return color.toString();
    }

    /** Color numbers based on its capacity */
    public static String colorCapacity(int current, int total) {
        ChatColor color;


        // danger dark red
        if (current >= total) {
            color = ChatColor.DARK_RED;
        }
        // danger red
        else if (current >= total - 1) {
            color = ChatColor.RED;
        }
        // warning orange/gold
        else if (current >= total - 2) {
            color = ChatColor.GOLD;
        }
        // warning yellow
        else if (current >= total - 3) {
            color = ChatColor.YELLOW;
        }
        // good green
        else {
            color = ChatColor.GREEN;
        }

        return MessageUtil.replaceColors(color.toString() + current);
    }

    public static String formatSeparators(String format, ChatColor prefix, ChatColor suffix) {
        format = format.replace("", "") // no reason for this just to keep real one bellow
            .replaceAll("\\.", suffix + "." + prefix)
            .replaceAll("\\-", suffix + "-" + prefix)
            .replaceAll("_", suffix + "_" + prefix);

        //format = format.replaceAll("(\\.|\\-|_)", suffix + "%1" + prefix);

        //LogUtil.debug(format);
        return MessageUtil.replaceColors(prefix + format + suffix);
    }

    /** Make any string to a simple truncated message */
    public static String shortMessage(int size, String message) {
        int length = message.length();
        int last = size;
        String shortMsg = message.substring(0, (length > size ? size : length));

        while (shortMsg.endsWith(" ")) {
            shortMsg = shortMsg.substring(0, --last);
        }

        if (length > size) {
            return shortMsg.substring(0, shortMsg.length() - 3) + "...";
        }
        else {
            return shortMsg;
        }
    }

    /** Center the location and maintain pitch and yaw */
    public static Location center(Location location) {
        Location centered = location.clone();

        centered.setX(location.getBlockX() + 0.5);
        centered.setY(location.getBlockY() + 0.5);
        centered.setZ(location.getBlockZ() + 0.5);

        return centered;
    }

    /** Center text between lines */
    public static String textLine(String text, int size, char delimiter) {
        return textLine(text, size, delimiter, "&7&m", "&a");
    }

    /** Center text between lines */
    public static String textLine(String text, int size, char delimiter, String lineColor, String textColor) {
        int padding = Math.abs(MessageUtil.stripColors(text).length() - size) / 2;
        StringBuilder side = new StringBuilder();

        for (int i = 0; i < padding; i++) {
            side.append(delimiter);
        }

        return MessageUtil.message("%s%s%s %s %s%s", lineColor, side, textColor, text, lineColor, side);
    }

    /** Hard truncate the string at the end to the specific size */
    public static String truncate(String text, int size) {
        return text.substring(0, text.length() > size ? size : text.length());
    }

    /** Hard truncate the string at the start to the specific size */
    public static String shrink(String text, int size) {
        return text.substring(text.length() > size ? size - text.length() : 0, text.length());
    }

    /** Converts string to upper case camel from underscore spaced string */
    public static String toUpperSpacedCamel(String string) {
        char[] text = string.toLowerCase().toCharArray();
        boolean upper = true;

        for (int i = 0; i < text.length; i++) {
            if (upper) {
                text[i] = Character.toUpperCase(text[i]);
                upper = false;
            }

            if (text[i] == '_') {
                upper = true;
                text[i] = ' ';
            }
        }

        return new String(text);
    }

    /** add a format to preformated color message */
    public static String fcolor(ChatColor color, String message) {
        return message.replaceAll(ChatColor.COLOR_CHAR + "([0-9a-fA-F])", "&$1" + color.toString());
    }

    /** Convert a point to a vector */
    public static Vector pointToVector(Point point) {
        return new Vector(point.getX(), point.getY(), point.getZ());
    }

    /** Santize the string to be used in packets */
    public static String sanitize(String text) {
        if (text == null || text.length() == 0) {
            return "\"\"";
        }

        char c;
        int i;
        int len = text.length();
        StringBuilder sb = new StringBuilder(len + 4);
        String t;
        sb.append('"');

        for (i = 0; i < len; i += 1) {
            c = text.charAt(i);

            switch (c) {
                case '\\':
                case '"':
                    sb.append('\\');
                    sb.append(c);
                    break;
                case '/':
                    sb.append('\\');
                    sb.append(c);
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                default:
                    if (c < ' ') {
                        t = "000" + Integer.toHexString(c);
                        sb.append("\\u" + t.substring(t.length() - 4));
                    }
                    else {
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
        return sb.toString();
    }

    public static int chars(String string) {
        String finalString = "";

        for (int i = 0; i < string.toCharArray().length ; i++) {
            if (finalString.length() > 6) break;

            finalString += (int) Ascii.toUpperCase(string.toCharArray()[i]);
        }

        return Integer.valueOf(finalString);
    }

    public static Vector randomOffset() {
        return new Vector(rand.nextDouble(), rand.nextDouble(), rand.nextDouble());
    }

    private static Map<NodePlayer, BukkitTask> actionBarAnimations = new HashMap<>();

    /** Send a cool animated action bar message */
    @Synchronized("actionBarAnimations")
    public static void sendAnimatedActionBar(GamePlayer player, String message) {
        final NodePlayer nodePlayer = (NodePlayer) player;

        Clocker animation = new Clocker(MathUtil.ticks(4)) {
            @Override
            public void runTock(int position) {
                String[] prefix = new String[] {"&e&l>>> &f", "&e&l>>&0&l> &f", "&e&l>&0&l>&e&l> &f","&0&l>&e&l>> &f"};
                String[] suffix = new String[] {" &e&l<<<", " &0&l<&e&l<&e&l<", " &e&l<&0&l<&e&l<", " &e&l<<&0&l<"};
                int pos = position / 20;

                nodePlayer.sendActionbarMessage(MessageUtil.replaceColors(prefix[pos]) + message + MessageUtil.replaceColors(suffix[pos]));
            }

            @Override
            public void runLast(int position) {
                actionBarAnimations.remove(nodePlayer);
            }
        };

        // Make sure the player is running 1.8 or newer
        if (PacketHacks.isTitleAble(player.getPlayer())) {
            if (actionBarAnimations.containsKey(nodePlayer)) {
                actionBarAnimations.remove(nodePlayer).cancel();
            }

            actionBarAnimations.put(nodePlayer, animation.run());
        }
        else {
            nodePlayer.sendMessage(message);
        }
    }
}
