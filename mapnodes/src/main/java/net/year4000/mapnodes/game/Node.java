/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.game;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.NodeFactory;
import net.year4000.mapnodes.Settings;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.exceptions.InvalidJsonException;
import net.year4000.mapnodes.api.exceptions.WorldLoadException;
import net.year4000.mapnodes.api.game.GameConfig;
import net.year4000.mapnodes.clocks.RestartServer;
import net.year4000.mapnodes.game.system.SpectatorKit;
import net.year4000.mapnodes.game.system.SpectatorTeam;
import net.year4000.mapnodes.map.CoreMapObject;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.*;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.CachedServerIcon;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class Node {
    private static final File WORLD_CONTAINER = new File(Bukkit.getWorldContainer(), MapNodesPlugin.getInst().getName());
    private static final String TEMPLATE = "%d-%s";
    private static final int ICON_SIZE = 64;
    /** Stores the info about the map's json */
    private final JsonObject mapJson;
    /** The node's id */
    private int id;
    private CachedServerIcon icon;
    private Image iconImage;
    private NodeGame game;
    /** Manages the node's worlds */
    private String worldName;
    private ZipFile worldFile;
    private World world;
    private URL worldUrl;
    private File worldCache;
    private int worldSize;

    public Node(int id, CoreMapObject worldFolder) throws WorldLoadException, InvalidJsonException {
        this.id = id;
        mapJson = worldFolder.getMap();

        try {
            worldName = String.format(TEMPLATE, id, worldFolder.getObject().getURLName());
            worldUrl = new URL(worldFolder.getWorld().getUrl() + "?key=" + Settings.get().getKey());
            worldCache = new File(new File(Settings.get().getWorldCache()), worldFolder.getCacheId() + ".zip");
            worldFile = new ZipFile(worldCache);
            worldSize = worldFolder.getWorld().getSize();
        }
        catch (MalformedURLException | ZipException e) {
            // Can not happen unless net.year4000.mapnodes.api is broke
        }

        // Load icon if one
        if (worldFolder.getIcon() != null) {
            try {
                // Original size
                BufferedImage bufferedImage = ImageIO.read(new URL(worldFolder.getIcon().getUrl()));

                // Resize to 64 x 64
                BufferedImage bufferedIcon = new BufferedImage(ICON_SIZE, ICON_SIZE, BufferedImage.TYPE_INT_RGB);
                Graphics graphic = bufferedIcon.createGraphics();
                graphic.drawImage(bufferedImage, 0, 0, ICON_SIZE, ICON_SIZE, null);
                graphic.dispose();

                icon = Bukkit.loadServerIcon(bufferedIcon);
                iconImage = bufferedIcon;
            }
            catch (Exception e) {
                MapNodesPlugin.debug(e.getMessage());
            }
        }

        // Load Map.json
        try {
            MapNodesPlugin.debug(Msg.util("debug.map.validate", worldFolder.getObject().getName()));

            // Validate the map with the default world for validation that needs world locations
            World world = Bukkit.getWorlds().get(0);
            GsonUtil.createGson(world).fromJson(mapJson, NodeGame.class).validate();

            // Register the map json to NodeGame
            game = GsonUtil.createGson().fromJson(mapJson, NodeGame.class);
            game.getMap().convertAuthors(); // Fetches UUIDs for cache
        }
        catch (JsonIOException | JsonSyntaxException e) {
            throw new InvalidJsonException(e.getMessage());
        }
    }

    /** Delete stray maps created by MapNodes */
    public static void removeStrayMaps() {
        if (WORLD_CONTAINER.exists()) {
            FileUtils.deleteQuietly(WORLD_CONTAINER);
        }
    }

    /** Register this node */
    public void register() {
        MapNodesPlugin.debug(Msg.util("node.register"));
        try {
            if (getWorld() == null) {
                unZip();

                if (Bukkit.isPrimaryThread()) {
                    createWorld();
                }
                else {
                    SchedulerUtil.runSync(this::createWorld);
                }
            }
            else {
                throw new WorldLoadException(Msg.util("error.world.loaded"));
            }

            while (getWorld() == null) {
                Common.sleep(250, TimeUnit.MILLISECONDS);
            }

            game = GsonUtil.createGson(getWorld()).fromJson(mapJson, NodeGame.class);
            game.getMap().convertAuthors(); // Uses cached UUIDs

            // register system team and kit
            if (game.getTeams().containsKey(NodeTeam.SPECTATOR)) {
                game.getTeams().remove(NodeTeam.SPECTATOR);
            }

            game.getTeams().put(NodeTeam.SPECTATOR, new SpectatorTeam(game.getConfig().getSpawn()));

            if (game.getKits().containsKey(NodeTeam.SPECTATOR)) {
                game.getKits().remove(NodeTeam.SPECTATOR);
            }

            game.getKits().put(NodeTeam.SPECTATOR, new SpectatorKit());

            // Load global kits
            Type kitType = new TypeToken<Map<String, NodeKit>>() {
            }.getType();
            Map<String, NodeKit> globalKits = GsonUtil.createGson().fromJson(Settings.get().getKits(), kitType);
            if (globalKits != null) {
                globalKits.forEach((key, kit) -> game.getKits().put(key, kit));
            }
        }
        catch (JsonIOException | JsonSyntaxException | WorldLoadException e) {
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
                    if (count < 10) {
                        PacketHacks.respawnPlayer(player);
                    }
                    else if (count < 20) {
                        player.teleport(MapNodes.getCurrentWorld().getSpawnLocation());
                    }
                    else {
                        SchedulerUtil.runSync(() -> player.kickPlayer(Msg.locale(player, "error.cmd.error")));
                    }

                    if (player.isOnline()) {
                        online = true;
                    }
                }

                if (!online) {
                    break;
                }
            }

            unloadWorld();
            deleteWorld();
        });
    }

    /** Unzip the map's files */
    public void unZip() throws WorldLoadException {
        File location = new File(WORLD_CONTAINER, worldName);

        if (location.exists()) {
            throw new WorldLoadException(Msg.util("error.world.exists"));
        }

        try {
            if (!worldCache.exists()) {
                FileUtils.copyURLToFile(worldUrl, worldCache);
            }

            worldFile.extractAll(location.getPath());
            MapNodesPlugin.debug(Msg.util("debug.world.unzip", worldName));
        }
        catch (ZipException | IOException e) {
            throw new WorldLoadException(e.getMessage());
        }
    }

    /** Create the world for the world. */
    public void createWorld() {
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
        }
        catch (IOException e) {
            MapNodesPlugin.debug(e.getMessage());
        }
    }

    public JsonObject getMapJson() {
        return this.mapJson;
    }

    public int getId() {
        return this.id;
    }

    public CachedServerIcon getIcon() {
        return this.icon;
    }

    public Image getIconImage() {
        return this.iconImage;
    }

    public NodeGame getGame() {
        return this.game;
    }

    public String getWorldName() {
        return this.worldName;
    }

    public ZipFile getWorldFile() {
        return this.worldFile;
    }

    public World getWorld() {
        return this.world;
    }

    public URL getWorldUrl() {
        return this.worldUrl;
    }

    public File getWorldCache() {
        return this.worldCache;
    }

    public int getWorldSize() {
        return this.worldSize;
    }
}
