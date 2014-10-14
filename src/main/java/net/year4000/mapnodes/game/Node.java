package net.year4000.mapnodes.game;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.NodeFactory;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.GameConfig;
import net.year4000.mapnodes.clocks.RestartServer;
import net.year4000.mapnodes.exceptions.InvalidJsonException;
import net.year4000.mapnodes.exceptions.WorldLoadException;
import net.year4000.mapnodes.game.system.SpectatorKit;
import net.year4000.mapnodes.game.system.SpectatorTeam;
import net.year4000.mapnodes.map.MapFolder;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.CachedServerIcon;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Getter
public class Node {
    private static final File WORLD_CONTAINER = new File(Bukkit.getWorldContainer(), MapNodesPlugin.getInst().getName());
    private static final String TEMPLATE = "%d-%s";

    /** The node's id */
    private int id;

    /** Stores the info about the map's json */
    private final File mapJson;
    private CachedServerIcon icon;
    private Image iconImage;
    private NodeGame game;

    /** Manages the node's worlds */
    private String worldName;
    private ZipFile worldFile;
    private World world;

    public Node(int id, MapFolder worldFolder) throws WorldLoadException, InvalidJsonException {
        this.id = id;
        mapJson = worldFolder.getMap();

        // Load the world

        try {
            worldName = String.format(TEMPLATE, id, worldFolder.getName());
            worldFile = new ZipFile(worldFolder.getWorld());
        } catch (ZipException e) {
            MapNodesPlugin.log(e.getMessage());
        }

        // Load icon if one
        if (worldFolder.getIcon() != null) {
            try {
                icon = Bukkit.loadServerIcon(worldFolder.getIcon());
                iconImage = ImageIO.read(worldFolder.getIcon());
            } catch (Exception e) {
                MapNodesPlugin.debug(e.getMessage());
            }
        }

        // Load Map.json
        try {
            MapNodesPlugin.debug(Msg.util("debug.map.validate", worldFolder.getName()));

            // Validate the map with the default world for validation that needs world locations
            World world = Bukkit.getWorlds().get(0);
            GsonUtil.createGson(world).fromJson(loadMap(), NodeGame.class).validate();

            // Register the map json to NodeGame
            game = GsonUtil.createGson().fromJson(loadMap(), NodeGame.class);
        } catch (JsonIOException | JsonSyntaxException e) {
            throw new InvalidJsonException(e.getMessage());
        }
    }

    /** Register this node */
    public void register() {
        try {
            if (getWorld() == null) {
                unZip();
                createWorld();
            }

            game = GsonUtil.createGson(getWorld()).fromJson(loadMap(), NodeGame.class);

            // register system team and kit
            if (game.getTeams().containsKey("spectator")) {
                game.getTeams().remove("spectator");
            }
            game.getTeams().put("spectator", new SpectatorTeam());

            if (game.getKits().containsKey("spectator")) {
                game.getKits().remove("spectator");
            }
            game.getKits().put("spectator", new SpectatorKit());
            game.getKits().put("default", new NodeKit());

        } catch (JsonIOException | JsonSyntaxException | WorldLoadException e) {
            MapNodesPlugin.debug(e, false);

            if (game.getStopClock() != null) {
                game.getStopClock().cancel();
            }

            // Handle failed maps better to fix exceptions
            if (NodeFactory.get().isQueuedGames()) {
                NodeFactory.get().loadNextQueued();
            }
            else {
                new RestartServer(MathUtil.ticks(10)).run();
            }
        }
    }

    /** Unregister this node, this includes things related to the world */
    public void unregister() {
        if (!Bukkit.getPluginManager().isPluginEnabled(MapNodesPlugin.getInst())) return;
        checkNotNull(world);

        SchedulerUtil.runAsync(() -> {
            int count = 0;
            while (getWorld().getPlayers().size() > 0) {
                ++count;

                if (count % 1000000 == 0) {
                    MapNodesPlugin.debug(Msg.util("debug.world.remove", getWorld().getName()));
                }

                boolean online = false;

                for (Player player : getWorld().getPlayers()) {
                    player.teleport(MapNodes.getCurrentWorld().getSpawnLocation());

                    if (player.isOnline()) {
                        online = true;
                    }
                }

                if (!online) break;
            }

            unloadWorld();
            deleteWorld();
        });
    }

    /** Reads the map.json and return the reader stream */
    private Reader loadMap() {
        try {
            return new FileReader(mapJson);
        } catch (IOException e) {
            MapNodesPlugin.debug("Should not see this, you should of ran checks before.");
            MapNodesPlugin.debug(e.getMessage());
        }

        return null;
    }

    /** Unzip the map's files */
    public void unZip() throws WorldLoadException {
        File location = new File(WORLD_CONTAINER, worldName);

        if (location.exists()) {
            throw new WorldLoadException(Msg.util("error.world.exists"));
        }

        try {
            worldFile.extractAll(location.getPath());
            MapNodesPlugin.debug(Msg.util("debug.world.unzip", worldName));
        } catch (ZipException e) {
            throw new WorldLoadException(e.getMessage());
        }
    }

    /** Create the world for the world. */
    public void createWorld() throws WorldLoadException {
        if (world != null) {
            throw new WorldLoadException(Msg.util("error.world.loaded"));
        }

        GameConfig config = game.getConfig();

        // Create the world
        WorldCreator worldCreator = new WorldCreator(MapNodesPlugin.getInst().getName() + File.separator + worldName);
        worldCreator.generateStructures(false);
        worldCreator.environment(config.getEnvironment());
        worldCreator.generator(new NullGenerator());
        worldCreator.type(WorldType.FLAT);

        // Edit the world settings
        World world = worldCreator.createWorld();
        world.setKeepSpawnInMemory(false);
        world.setAutoSave(false);

        // Difficulty
        world.setDifficulty(config.getDifficulty());

        // Weather Rule
        world.setStorm(config.isWeather());
        world.setWeatherDuration(Integer.MAX_VALUE);

        // Time Rule
        world.setTime(config.getTimeLock().getTime());

        if (config.getTimeLock().getTime() != -1) {
            world.setGameRuleValue("doDaylightCycle", "false");
        }

        // Safe Spawn
        Location loc = config.getSpawn().get(0);
        world.setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        Block platform = world.getSpawnLocation().add(0, -1, 0).getBlock();

        if (platform.isEmpty() && MapNodesPlugin.getInst().getLog().isDebug()) {
            platform.setType(Material.BEDROCK);
        }

        this.world = world;
        MapNodesPlugin.debug(Msg.util("debug.world.loaded", worldName));
    }

    /** Unload the world from the system */
    public void unloadWorld() {
        checkArgument(world != null, Msg.util("error.world.unload"));

        Bukkit.unloadWorld(world, false);

        MapNodesPlugin.debug(Msg.util("debug.world.unloaded", worldName));
    }

    /** Delete the world from the system */
    public void deleteWorld() {
        checkArgument(world != null, Msg.util("error.world.unload"));

        File location = new File(WORLD_CONTAINER, worldName);

        try {
            FileUtils.deleteDirectory(location);
            MapNodesPlugin.debug(Msg.util("debug.world.deleted", worldName));
        } catch (IOException e) {
            MapNodesPlugin.debug(e.getMessage());
        }
    }

    /** Delete stray maps created by MapNodes */
    public static void removeStrayMaps() {
        if (WORLD_CONTAINER.exists()) {
            FileUtils.deleteQuietly(WORLD_CONTAINER);
        }
    }
}
