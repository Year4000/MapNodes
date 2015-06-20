/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.utils.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.year4000.mapnodes.utils.WorldTime;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The world time deserializer code logic taken from CommandBook.
 * https://github.com/sk89q/CommandBook/blob/master/src/main/java/com/sk89q/commandbook/util/InputUtil.java
 */
public class WorldTimeDeserializer implements JsonDeserializer<WorldTime> {
    protected static final Pattern TWELVE_HOUR_TIME = Pattern.compile("^([0-9]+(?::[0-9]+)?)([apmAPM\\.]+)$");

    public static int worldTime(String timeStr) {
        Matcher matcher;

        // Tick time
        if (timeStr.matches("^*[0-9]+$")) {
            return Integer.parseInt(timeStr.substring(1));

        }
        // Allow 24-hour time
        else if (timeStr.matches("^[0-9]+:[0-9]+$")) {
            String[] parts = timeStr.split(":");
            int hours = Integer.parseInt(parts[0]);
            int mins = Integer.parseInt(parts[1]);
            return (int) (((hours - 8) % 24) * 1000 + Math.round((mins % 60) / 60.0 * 1000));

        }
        // Or perhaps 12-hour time
        else if ((matcher = TWELVE_HOUR_TIME.matcher(timeStr)).matches()) {
            String time = matcher.group(1);
            String period = matcher.group(2);
            int shift;

            if (period.equalsIgnoreCase("am") || period.equalsIgnoreCase("a.m.")) {
                shift = 0;
            }
            else if (period.equalsIgnoreCase("pm") || period.equalsIgnoreCase("p.m.")) {
                shift = 12;
            }
            else {
                return 0;
            }

            String[] parts = time.split(":");
            int hours = Integer.parseInt(parts[0]);
            int mins = parts.length >= 2 ? Integer.parseInt(parts[1]) : 0;
            return (int) ((((hours % 12) + shift - 8) % 24) * 1000 + (mins % 60) / 60.0 * 1000);

            // Or some shortcuts
        }
        else if (timeStr.equalsIgnoreCase("dawn")) {
            return (6 - 8 + 24) * 1000;
        }
        else if (timeStr.equalsIgnoreCase("sunrise")) {
            return (7 - 8 + 24) * 1000;
        }
        else if (timeStr.equalsIgnoreCase("morning")) {
            return (24) * 1000;
        }
        else if (timeStr.equalsIgnoreCase("day")) {
            return (24) * 1000;
        }
        else if (timeStr.equalsIgnoreCase("midday") || timeStr.equalsIgnoreCase("noon")) {
            return (12 - 8 + 24) * 1000;
        }
        else if (timeStr.equalsIgnoreCase("afternoon")) {
            return (14 - 8 + 24) * 1000;
        }
        else if (timeStr.equalsIgnoreCase("evening")) {
            return (16 - 8 + 24) * 1000;
        }
        else if (timeStr.equalsIgnoreCase("sunset")) {
            return (21 - 8 + 24) * 1000;
        }
        else if (timeStr.equalsIgnoreCase("dusk")) {
            return (21 - 8 + 24) * 1000 + (int) (30 / 60.0 * 1000);
        }
        else if (timeStr.equalsIgnoreCase("night")) {
            return (22 - 8 + 24) * 1000;
        }
        else if (timeStr.equalsIgnoreCase("midnight")) {
            return (0 - 8 + 24) * 1000;
        }

        return 0;
    }

    @Override
    public WorldTime deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        return new WorldTime(element.getAsJsonPrimitive().isString() ? worldTime(element.getAsString()) : element.getAsInt());
    }
}
