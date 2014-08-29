package net.year4000.mapnodes.map;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import net.year4000.mapnodes.exceptions.InvalidMapException;
import net.year4000.mapnodes.messages.Msg;

import javax.annotation.Nullable;
import java.io.File;

@Data
@Setter(AccessLevel.MODULE)
public class MapFolder {
    /** The name of the folder */
    private String name;

    /** The file location for map.json */
    private File map;

    /** The file location for world.zip */
    private File world;

    /** The file for the icon */
    @Nullable
    private File icon;

    /** Create and check if this is a valid map folder */
    public MapFolder(File path) throws InvalidMapException {
        if (!isMapFolder(path)) {
            throw new InvalidMapException(Msg.util("error.world.folder", path.getName()));
        }

        name = path.getName();
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
        else {
            return false;
        }

        return true;
    }
}
