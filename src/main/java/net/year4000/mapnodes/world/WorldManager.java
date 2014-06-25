package net.year4000.mapnodes.world;

import com.ewized.utilities.core.util.FileUtil;
import com.google.common.io.Files;
import lombok.Getter;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import net.year4000.mapnodes.MapNodes;
import net.year4000.mapnodes.configs.MainConfig;
import net.year4000.mapnodes.game.GameManager;
import net.year4000.mapnodes.utils.MissingJsonElement;
import net.year4000.mapnodes.utils.PureRandom;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class WorldManager {
    /** WorldManager. */
    private static WorldManager inst = null;
    /** The list of games. */
    @Getter
    private Deque<GameManager> games = new ArrayDeque<>();
    /** The current game. */
    private GameManager currentGame = null;
    /** The internal match id counter */
    private static int match = 0;

    /** Add a game world to the games. */
    private WorldManager() {
        try {
            for (String map : copyWorlds(MainConfig.get().getMaxLoadMaps())) {
                loadMap(createWorld(map));
            }
        } catch (NullPointerException e) {
            MapNodes.log(e.getMessage());
        }
    }

    public static WorldManager get() {
        if (inst == null) {
            inst = new WorldManager();
        }
        return inst;
    }

    /** Copy and return the list of worlds copied. */
    public List<String> copyWorlds(int max) throws NullPointerException {
        List<String> worldMaps = new ArrayList<>();
        PureRandom rand = new PureRandom(MainConfig.get().getMapFolder().size());

        // Copy the map to the server.
        for (int i = 0; i < max; i++) {
            File world = MainConfig.get().getMapFolder().get(rand.nextInt());
            worldMaps.add(copyWorld(world.getName()));
        }

        return worldMaps;
    }

    /** Get the file for the map with the name. */
    public File getWorld(String name) {
        File map = null;

        for (File file : MainConfig.get().getMapFolder()) {
            if (file.getName().equalsIgnoreCase(name))
                map = file;
        }

        return map;
    }

    /** Copy a world by its name. */
    public String copyWorld(String mapName) {
        String worldName = null;
        try {
            File worldsFolder = Bukkit.getWorldContainer();
            File world = getWorld(mapName);

            // Load the map
            worldName = world.getName().replaceAll(" ", "_") + "-" + worldCounter();
            FileUtil.copy(world, new File(worldsFolder + File.separator + worldName));

            // Unzip the file so we can use it temporaly untill new system is in place
            ZipFile zipWorld = new ZipFile(worldsFolder + File.separator + worldName + File.separator + "world.zip");
            zipWorld.extractAll(worldsFolder + File.separator + worldName);
        } catch (IOException | ZipException e) {
            e.printStackTrace();
        }

        return worldName;
    }

    /** Create the world for the world. */
    public World createWorld(String name) {
        // Create the world
        WorldCreator worldCreator = new WorldCreator(name);
        worldCreator.generateStructures(false);
        worldCreator.generator(new NullGenerator());
        worldCreator.type(WorldType.FLAT);

        // Edit the world settings
        World world = worldCreator.createWorld();
        world.setKeepSpawnInMemory(false);
        world.setAutoSave(false);

        return world;
    }

    /** Load a map so we can manage it. */
    public boolean loadMap(World world) {
        try {
            long startTime = System.currentTimeMillis();

            games.addLast(new GameManager(world));

            world.setAutoSave(false);
            MapNodes.log(String.format(
                "Loaded map, %s in (%s ms)",
                world.getName(),
                (System.currentTimeMillis()-startTime)/100
            ));
        } catch (MissingJsonElement e) {
            MapNodes.log(e.getMessage());
            //e.printStackTrace();
            return false;
        } catch (FileNotFoundException e) {
            MapNodes.log("Could not find map.json");
            return false;
        }

        return true;
    }

    /** Unload a world so the server have resources. */
    public void unLoadMap(World world) {
        Bukkit.unloadWorld(world, false);
        try {
            FileUtils.deleteDirectory(world.getWorldFolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Get the current gameManager. */
    public GameManager getCurrentGame() {
        if (currentGame == null) {
            currentGame = games.pop();
        }
        return currentGame;
    }

    /** Get the current gameManager. */
    public GameManager getNextGame() {
        return games.peek();
    }

    /** Set the next game */
    public GameManager nextGame() {
        // Unload the last game's world, when all players left it.
        World lastGameWorld = currentGame.getWorld();
        Bukkit.getScheduler().runTask(MapNodes.getInst(), () -> {
            while (lastGameWorld.getPlayers().size() > 0);
            unLoadMap(lastGameWorld);
        });
        currentGame = games.pop();
        return currentGame;
    }

    /** Should we try to load a new map */
    public boolean isNextGame() {
        return games.size() > 0;
    }

    /** Get the world counter */
    private int worldCounter() {
        return ++match;
    }

    /** Remove the world from the system */
    public void removeGame(GameManager gameManager) {
        unLoadMap(gameManager.getWorld());
        games.remove(gameManager);
    }
}
