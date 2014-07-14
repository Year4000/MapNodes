package net.year4000.mapnodes;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.Config;
import net.cubespace.Yamler.Config.InvalidConfigurationException;
import net.year4000.mapnodes.utils.LogUtil;

import java.io.File;
import java.util.ArrayList;
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
            LogUtil.debug(e.getMessage());
            e.printStackTrace();
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

    @Comment("The max number of maps to load per session")
    private boolean debug = false;

    @Comment("The max number of maps to load per session")
    private int loadMaps = 10;

    @Comment("The folders paths to the maps")
    private List<String> mapsFolder = new ArrayList<String>() {{
        add("/y4k/maps/");
    }};

}