package net.year4000.mapnodes.game;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.exceptions.InvalidJsonException;
import net.year4000.mapnodes.game.components.NodeKit;
import net.year4000.mapnodes.game.system.SpectatorKit;
import net.year4000.mapnodes.game.system.SpectatorTeam;
import net.year4000.mapnodes.map.MapFolder;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.GsonUtil;
import net.year4000.mapnodes.utils.Validator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.util.CachedServerIcon;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class MatchManager implements Validator {
    private final String name;
    private final Node node;
    private final File mapFile;
    @Getter
    private CachedServerIcon icon;
    @Getter
    private Image iconImage;
    @Getter
    private NodeGame game;

    public MatchManager(Node node, MapFolder folder) {
        this.name = folder.getName();
        this.node = node;

        // Load icon if one
        if (folder.getIcon() != null) {
            try {
                icon = Bukkit.loadServerIcon(folder.getIcon());
                iconImage = ImageIO.read(folder.getIcon());
            } catch (Exception e) {
                MapNodesPlugin.debug(e.getMessage());
            }
        }

        // Load Map.json
        this.mapFile = folder.getMap();

    }

    private Reader loadMap() {
        try {
            return new FileReader(mapFile);
        } catch (IOException e) {
            MapNodesPlugin.debug("Should not see this, you should of ran checks before.");
            MapNodesPlugin.debug(e.getMessage());
        }

        return null;
    }

    public void validate() throws InvalidJsonException {
        try {
            MapNodesPlugin.debug(Msg.util("debug.map.validate", name));

            World world = Bukkit.getWorlds().get(0);
            GsonUtil.createGson(world).fromJson(loadMap(), NodeGame.class).validate();

            game = GsonUtil.createGson().fromJson(loadMap(), NodeGame.class);
        } catch (JsonIOException | JsonSyntaxException e) {
            throw new InvalidJsonException(e.getMessage());
        }

        //LogUtil.debug(new Gson().toJson(game));
    }

    /** Register the map with world spawns */
    public void register() {
        try {
            game = GsonUtil.createGson(node.getWorld().getWorld()).fromJson(loadMap(), NodeGame.class);

            // register external team and kit
            if (game.getTeams().containsKey("spectator")) {
                game.getTeams().remove("spectator");
            }
            game.getTeams().put("spectator", new SpectatorTeam());

            if (game.getKits().containsKey("spectator")) {
                game.getKits().remove("spectator");
            }
            game.getKits().put("spectator", new SpectatorKit());
            game.getKits().put("default", new NodeKit());


        } catch (JsonIOException | JsonSyntaxException e) {
            MapNodesPlugin.debug(e.getMessage());
        }
    }
}
