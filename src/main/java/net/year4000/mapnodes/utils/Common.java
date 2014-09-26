package net.year4000.mapnodes.utils;

import net.year4000.utilities.ChatColor;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.Location;

public final class Common {
    private Common() {
        // Utility Class
    }

    /** Color numbers based on its percent */
    private static int toggle = 0;

    public static String colorNumber(int current, int total) {
        double percent = ((double)current / (double)total) * 100;
        ChatColor color;

        //LogUtil.log(percent+"");

        // danger dark red
        if (percent < 10) {
            toggle++;
            color = toggle % 5 == 0 ? ChatColor.DARK_RED : ChatColor.RED;
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

        return MessageUtil.replaceColors(color.toString() + current);
    }

    /** Color numbers based on its capacity */
    public static String colorCapacity(int current, int total) {
        ChatColor color;

        // warning yellow
        if (current >= total - 3) {
            color = ChatColor.YELLOW;
        }
        // warning orange/gold
        else if (current >= total - 2) {
            color = ChatColor.GOLD;
        }
        // danger red
        else if (current >= total - 1) {
            color = ChatColor.RED;
        }
        // danger dark red
        else if (current >= total) {
            color = ChatColor.DARK_RED;
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
}
