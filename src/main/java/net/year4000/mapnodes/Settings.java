package net.year4000.mapnodes;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.utilities.config.Comment;
import net.year4000.utilities.config.Config;
import net.year4000.utilities.config.InvalidConfigurationException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class Settings extends Config {
    private static Settings inst = null;

    private Settings() {
        try {
            CONFIG_HEADER = new String[] { "MapNodes Configuration" };
            CONFIG_FILE = new File(MapNodesPlugin.getInst().getDataFolder(), "config.yml");
            init();
        } catch (InvalidConfigurationException e) {
            MapNodesPlugin.log(e, true);
        } catch (NullPointerException e) {
            // Should only be catch by their is no file by the plugin instance.
        }

        if (System.getProperty("maps", null) != null) {
            mapsFolder = Arrays.asList(System.getProperty("maps", null).split("(,|;)"));
        }
    }

    public static Settings get() {
        if (inst == null) {
            inst = new Settings();
        }
        return inst;
    }

    public void reload() {
        inst = new Settings();
    }

    @Comment("The url to pull the locales from")
    private String url = "https://raw.githubusercontent.com/Year4000/Locales/master/net/year4000/mapnodes/locales/";

    @Comment("The url to pull the locales from")
    private String systemUrl = "https://raw.githubusercontent.com/Year4000/Locales/master/net/year4000/mapnodes/system/";

    @Comment("The max number of maps to load per session")
    private int loadMaps = 10;

    @Comment("The folders paths to the maps")
    private List<String> mapsFolder = new ArrayList<String>() {{
        add("/y4k/maps/");
    }};

}
