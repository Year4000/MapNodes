/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.map;

import com.google.common.collect.Iterables;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import lombok.Getter;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.Settings;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapFactory {
    @Getter
    private static Map<String, MapObject> folders;

    /** Find and load all maps */
    public MapFactory() {
        folders = new ConcurrentLinkedHashMap.Builder<String, MapObject>()
            .maximumWeightedCapacity(Short.MAX_VALUE)
            .build();

        MapNodesPlugin.getInst().getApi().getMaps().forEach(path -> {
            String id = path.getName().toLowerCase().replace(" ", "-");
            folders.put(id, path);
        });

        //folders.forEach(System.out::println);
    }

    /** Get the mapfolder by name */
    @Nullable
    public static MapObject getMap(String name) {
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
    public static List<MapObject> getMaps(int number) {
        // Number is 0 use empty array list
        if (number == 0) {
            return new ArrayList<>();
        }

        // Enabled maps
        Stream<MapObject> enabledFolders = folders.values().parallelStream().filter(m -> !m.isDisabled() && (Settings.get().getModes().size() == 0 || Settings.get().getModes().contains(m.getURLCategory())));
        List<MapObject> maps = new ArrayList<>(enabledFolders.collect(Collectors.toList()));

        // Reverse and shuffle the maps based on the number of maps
        for (int i = 0; i < maps.size(); i++) {
            Collections.reverse(maps);
            Collections.shuffle(maps);
        }

        // Loaded maps
        Iterator<MapObject> mapFolderIterator = Iterables.cycle(maps).iterator();
        ArrayList<MapObject> loadedMaps = new ArrayList<>();

        for (int i = 0; i < number; i++) {
            if (mapFolderIterator.hasNext()) {
                loadedMaps.add(mapFolderIterator.next());
            }
        }

        return loadedMaps;
    }
}
