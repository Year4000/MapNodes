package net.year4000.mapnodes.map;

import com.google.common.collect.Iterables;
import lombok.Getter;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.Settings;
import net.year4000.mapnodes.api.exceptions.InvalidMapException;
import net.year4000.mapnodes.messages.Msg;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

public class MapFactory {
    @Getter
    private static Map<String, MapFolder> folders;

    /** Find and load all maps */
    public MapFactory() {
        folders = new ConcurrentHashMap<>();

        Settings.get().getMapsFolder().parallelStream().forEach(path -> {
            File maps = new File(path);

            for (File map : checkNotNull(maps.listFiles())) {
                try {
                    maps(folders, map);
                }
                catch (SecurityException e) {
                    MapNodesPlugin.debug(e.getMessage());
                }
            }
        });

        //folders.forEach(System.out::println);
    }

    private static void maps(Map<String, MapFolder> folders, File maps) {
        try {
            if (maps.isDirectory()) {
                for (File world : checkNotNull(maps.listFiles())) {
                    try {
                        MapNodesPlugin.debug(Msg.util("debug.map.loaded", world.getName()));
                        folders.put(world.getName(), new MapFolder(world));
                    }
                    catch (InvalidMapException e) {
                        MapNodesPlugin.debug(e.getMessage());
                    }
                }
            }
        }
        catch (SecurityException e) {
            MapNodesPlugin.debug(e.getMessage());
        }
    }

    /** Get the mapfolder by name */
    @Nullable
    public static MapFolder getMap(String name) {
        if (isMap(name, true)) {
            return folders.get(name);
        }

        return null;
    }

    /** Check if the following map name exists */
    public static boolean isMap(String name) {
        return isMap(name, false);
    }

    /** Check if the following map name exists */
    public static boolean isMap(String name, boolean findDisabled) {
        if (!findDisabled) {
            return folders.containsKey(name) && !folders.get(name).isDisabled();
        }
        else {
            return folders.containsKey(name);
        }
    }

    /** A shuffle list of allowed maps */
    public static List<MapFolder> getMaps(int number) {
        Stream<MapFolder> enabledFolders = folders.values().parallelStream().filter(m -> !m.isDisabled() && (Settings.get().getModes().size() == 0 || Settings.get().getModes().contains(m.getParent())));
        List<MapFolder> maps = new ArrayList<>(enabledFolders.collect(Collectors.toList()));

        // Reverse and shuffle the maps based on the number of maps
        for (int i = 0; i < maps.size(); i++) {
            Collections.reverse(maps);
            Collections.shuffle(maps);
        }

        Iterator<MapFolder> mapFolderIterator = Iterables.cycle(maps).iterator();
        ArrayList<MapFolder> loadedMaps = new ArrayList<>();

        for (int i = 0; i < number; i++) {
            if (mapFolderIterator.hasNext()) {
                loadedMaps.add(mapFolderIterator.next());
            }
        }

        return loadedMaps;
    }
}
