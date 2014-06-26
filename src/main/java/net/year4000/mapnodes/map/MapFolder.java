package net.year4000.mapnodes.map;


import lombok.Data;
import net.year4000.mapnodes.map.exceptions.InvalidMapException;
import net.year4000.mapnodes.messages.Msg;

import java.io.File;

@Data
public class MapFolder {
    /** The file location for map.json */
    private File map;

    /** The file location for world.zip */
    private File world;

    /** The file for the icon */
    private File icon;

    /** Create and check if this is a valid map folder */
    public MapFolder(File path) throws InvalidMapException {
        if (!isMapFolder(path)) {
            throw new InvalidMapException(Msg.util("error.folder"));
        }

        map = new File(path, "map.json");
        world = new File(path, "world.zip");
        icon = new File(path, "icon.png");

        if (!(icon.exists() && icon.isFile() && icon.canRead())) {
            icon = null;
        }
    }

    /** Check is the folder is a map we can use */
    public boolean isMapFolder(File path) {
        String[] files = {"map.json", "world.zip"};

        if (path.exists() && path.isDirectory() && path.canRead()) {
            for (String fileName : files) {
                File file = new File(path, fileName);
                if (!(file.exists() && file.isFile() && file.canRead())) return false;
            }
        }

        return false;
    }
}
