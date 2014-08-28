package net.year4000.mapnodes.map;


import lombok.Getter;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.Settings;
import net.year4000.mapnodes.exceptions.InvalidMapException;
import net.year4000.mapnodes.messages.Msg;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkNotNull;

public class MapFactory {
    @Getter
    private static Map<String, MapFolder> folders;

    /** Find and load all maps */
    public MapFactory() {
        folders = new ConcurrentHashMap<>();

        Settings.get().getMapsFolder().parallelStream().forEach(path -> {
            try {
                File maps = new File(path);
                if (maps.isDirectory()) {
                    for (File world : checkNotNull(maps.listFiles())) {
                        try {
                            MapNodesPlugin.debug(Msg.util("debug.map.loaded"), world.getName());
                            folders.put(world.getName(), new MapFolder(world));
                        } catch (InvalidMapException e) {
                            MapNodesPlugin.debug(e.getMessage());
                        }
                    }
                }
            } catch (SecurityException e) {
                MapNodesPlugin.debug(e.getMessage());
            }
        });

        //folders.forEach(System.out::println);
    }

    /** Get the mapfolder by name */
    @Nullable
    public static MapFolder getMap(String name) {
        if (isMap(name)) {
            return folders.get(name);
        }

        return null;
    }

    /** Check if the following map name exists */
    public static boolean isMap(String name) {
        return folders.containsKey(name);
    }

    /** A shuffle list of allowed maps */
    public static List<MapFolder> getMaps(int number) {
        List<MapFolder> maps = new ArrayList<>(folders.values());
        Collections.shuffle(maps);

        return new ArrayList<MapFolder>() {{
            for (int i = 0; i < (number > maps.size() ? maps.size() : number); i++) {
                add(maps.get(i));
            }
        }};
    }
}
