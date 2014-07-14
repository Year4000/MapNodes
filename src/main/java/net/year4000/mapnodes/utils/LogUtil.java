package net.year4000.mapnodes.utils;

import com.ewized.utilities.bukkit.util.MessageUtil;
import net.year4000.mapnodes.MapNodesPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogUtil {
    public static boolean debug = false;
    private static final Logger log = MapNodesPlugin.getInst().getLogger();

    /** Logs a message to the console */
    public static void log(String message, Object... args) {
        log.log(Level.INFO, String.format(MessageUtil.stripColors(message), args));
    }

    /** Logs a debug message to the console */
    public static void debug(String message, Object... args) {
        if (debug) {
            log("DEBUG: " + MessageUtil.stripColors(message), args);
        }
    }

    /** Print out the stack trace */
    public static void debug(Exception e, boolean simple) {
        if (debug) {
            debug(e.getMessage());

            if (!simple) {
                for (StackTraceElement element : e.getStackTrace()) {
                    debug(element.toString());
                }
            }
        }
    }

    /** Print out the stack trace */
    public static void log(Exception e, boolean simple) {
        log(e.getMessage());

        if (!simple) {
            for (StackTraceElement element : e.getStackTrace()) {
                log(element.toString());
            }
        }
    }
}
