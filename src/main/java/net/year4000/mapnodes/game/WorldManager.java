package net.year4000.mapnodes.game;

import lombok.Getter;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.Settings;
import net.year4000.mapnodes.api.game.GameConfig;
import net.year4000.mapnodes.exceptions.WorldLoadException;
import net.year4000.mapnodes.map.MapFolder;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.NullGenerator;
import org.bukkit.*;
import org.bukkit.block.Block;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;

public class WorldManager {
    private static final File WORLD_CONTAINER = new File(Bukkit.getWorldContainer(), MapNodesPlugin.getInst().getName());
    private static final String TEMPLATE = "%d-%s";
    private final String worldName;
    private final Node node;
    private ZipFile worldFile;
    @Getter
    private World world;

    public WorldManager(Node node, MapFolder folder) {
        this.node = node;
        this.worldName = String.format(TEMPLATE, node.getId(), folder.getName());

        try {
            worldFile = new ZipFile(folder.getWorld());
        } catch (ZipException e) {
            MapNodesPlugin.log(e.getMessage());
        }
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

        GameConfig config = node.getMatch().getGame().getConfig();

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
        world.setDifficulty(Difficulty.getByValue(config.getDifficulty()));

        // Weather Rule
        world.setStorm(config.isWeather());
        world.setWeatherDuration(Integer.MAX_VALUE);

        // Time Rule
        world.setTime(config.getTimeLock());

        if (config.getTimeLock() != -1) {
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
