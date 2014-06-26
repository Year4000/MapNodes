package net.year4000.mapnodes.utils;

import net.year4000.mapnodes.MapNodesPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogUtil {
    public static boolean debug = false;
    private static final Logger log = MapNodesPlugin.getInst().getLogger();

    /** Logs a message to the console */
    public static void log(String message, Object... args) {
        log.log(Level.INFO, String.format(message, args));
    }

    /** Logs a debug message to the console */
    public static void debug(String message, Object... args) {
        if (debug) {
            log("DEBUG: " + message, args);
        }
    }
}
