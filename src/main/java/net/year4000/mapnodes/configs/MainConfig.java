package net.year4000.mapnodes.configs;

import lombok.*;
import net.cubespace.Yamler.Config.Config;
import net.cubespace.Yamler.Config.InvalidConfigurationException;
import net.year4000.mapnodes.MapNodes;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

@Data
@EqualsAndHashCode(callSuper=false)
@SuppressWarnings("all")
public class MainConfig extends Config {
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private static MainConfig inst = null;

    /** Set up config using the singleton pattern. */
    private MainConfig() {
        try {
            CONFIG_HEADER = new String[] {"MapNodes Configuration"};
            CONFIG_FILE = new File(MapNodes.getInst().getDataFolder(), "config.yml");
            init();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    /** Get the current instance of the config. */
    public static MainConfig get() {
        if (getInst() == null)
            setInst(new MainConfig());
        return getInst();
    }

    /** The location of the maps. */
    private List<String> mapsFolder = new ArrayList<String>() {{
        add("/y4k/maps/");
    }};

    /** return the maps as a file array. */
    public List<File> getMapFolder() {
        List<File> folders = new ArrayList<>();

        for (String path : mapsFolder) {
            File mapsFolder = new File(path);

            for (File file : checkNotNull(mapsFolder.listFiles(), "Not a valid folder path."))
                folders.add(file);
        }

        return folders;
    }

    /** Max maps to load for the server. */
    private int maxLoadMaps = 10;

    /** World lock timer in secs. */
    private int worldLockDelay = 10;

    /** The list of items the player will have. */
    // TODO: Add config for spectator
}
