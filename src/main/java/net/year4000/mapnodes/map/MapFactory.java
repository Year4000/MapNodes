package net.year4000.mapnodes.map;


import com.google.common.base.Preconditions;
import lombok.Getter;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.map.exceptions.InvalidMapException;
import net.year4000.mapnodes.utils.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MapFactory {
    @Getter
    private final List<MapFolder> folders = new ArrayList<>();

    /** Find and load all maps */
    public MapFactory() {
        MapNodesPlugin.getInst().getConfig().getMapsFolder().forEach(path -> {
            try {
                File maps = new File(path);
                if (maps.isDirectory()) {
                    for (File world : Preconditions.checkNotNull(maps.listFiles())) {
                        try {
                            folders.add(new MapFolder(world));
                        } catch (InvalidMapException e) {
                            LogUtil.debug(e.getMessage());
                        }
                    }
                }
            } catch (SecurityException e) {
                LogUtil.debug(e.getMessage());
            }
        });
    }

}
