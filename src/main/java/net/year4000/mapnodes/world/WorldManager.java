package net.year4000.mapnodes.world;

import com.ewized.utilities.core.util.FileUtil;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.google.common.base.Preconditions.checkNotNull;

@Data
public class WorldManager {
    /** WorldManager. */
    private static WorldManager inst = null;
    /** The list of games. */
    private List<GameManager> games = new ArrayList<>();
    /** The current game. */
    private int currentIndex = 0;

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
            worldName = world.getName() + "-" + System.currentTimeMillis();
            FileUtil.copy(world, new File(worldsFolder + File.separator + worldName));
        } catch (IOException e) {
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
    public boolean loadMap(World world, Integer index) {
        try {
            long startTime = System.currentTimeMillis();

            if (index != null)
                games.add(index, new GameManager(world));
            else
                games.add(new GameManager(world));

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

    /** Load a map so we can manage it. */
    public boolean loadMap(World world) {
        return loadMap(world, null);
    }

    /** Unload a world so the server have resources. */
    public void unLoadMap(World world) {
        Bukkit.unloadWorld(world, false);
    }

    /** Get the current gameManager. */
    public GameManager getCurrentGame() {
        return games.get(currentIndex);
    }

    /** Get the current gameManager. */
    public GameManager getNextGame() {
        int map = currentIndex + 1 == getGames().size() ? 0 : currentIndex + 1;
        return games.get(map);
    }

    /** Get the current gameManager. */
    public GameManager getLastGame() {
        int map = currentIndex - 1 < 0 ? getGames().size() - 1 : currentIndex - 1;
        return games.get(map);
    }
}
