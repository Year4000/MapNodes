package net.year4000.mapnodes.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.Config;
import net.cubespace.Yamler.Config.InvalidConfigurationException;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.LogUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import static com.google.common.base.Preconditions.checkNotNull;

@Data
@EqualsAndHashCode(callSuper=false)
public class Settings extends Config {
    public Settings() {
        try {
            CONFIG_HEADER = new String[] {"MapNodes Configuration"};
            CONFIG_FILE = new File(MapNodesPlugin.getInst().getDataFolder(), "config.yml");
            init();
        } catch (InvalidConfigurationException e) {
            LogUtil.debug(e.getMessage());
            e.printStackTrace();
        }
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
