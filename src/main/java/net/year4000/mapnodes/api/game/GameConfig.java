package net.year4000.mapnodes.api.game;

import net.year4000.mapnodes.utils.WorldTime;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;

import java.net.URL;
import java.util.List;

public interface GameConfig {
    /**
     * Get the world environment of the world.
     *
     * @return The world environment.
     */
    public World.Environment getEnvironment();

    /**
     * Get the difficulty of the world.
     *
     * @return The difficulty level.
     */
    public Difficulty getDifficulty();

    /**
     * Get the time the world should be locked at.
     *
     * @return The world time.
     */
    public WorldTime getTimeLock();

    /**
     * Should the weather be turned on.
     *
     * @return The state of the weather.
     */
    public boolean isWeather();

    /**
     * Get the max world height of the world.
     *
     * @return The world height of the world.
     */
    public int getWorldHeight();

    /**
     * Get the URL to the resource pack.
     *
     * @return The URL to the resource pack.
     */
    public URL getResourcePack();


    /**
     * The spawn locations when players enter the world.
     *
     * @return
     */
    public List<Location> getSpawn();

    public Location getSafeRandomSpawn();
}
