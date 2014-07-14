package net.year4000.mapnodes.api.game;

import net.year4000.mapnodes.api.game.configs.GameBow;
import net.year4000.mapnodes.api.game.configs.GameChest;
import net.year4000.mapnodes.api.game.configs.GameTNT;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;

import java.net.URL;
import java.util.List;

public interface GameConfig {
    /**
     * Get the world environment of the world.
     * @return The world environment.
     */
    public World.Environment getEnvironment();

    /**
     * Get the difficulty of the world.
     * @return The difficulty level.
     */
    public int getDifficulty();

    /**
     * Get the time the world should be locked at.
     * @return The world time.
     */
    public long getTimeLock();

    /**
     * Should the weather be turned on.
     * @return The state of the weather.
     */
    public boolean isWeather();

    /**
     * Is the map destructible.
     * @return Can the world be destructible.
     */
    public boolean isDestructible();

    /**
     * Get the max world height of the world.
     * @return The world height of the world.
     */
    public int getWorldHeight();

    /**
     * Get the URL to the resource pack.
     * @return The URL to the resource pack.
     */
    public URL getResourcePack();

    /**
     * The damage causes that the player will not be hurt by.
     * @return The list of damage causes.
     */
    public List<EntityDamageEvent.DamageCause> getNoDamage();

    /**
     * Get the list of mobs allow to spawn in the world.
     * @return The list of entity type that can spawn.
     */
    public List<EntityType> getEnabledMobs();

    /**
     * The materials that can be dropped from the player .
     * @return The list of materials.
     */
    public List<Material> getPlayerDrops();

    /**
     * The materials that can be dropped from blocks.
     * @return The list of materials.
     */
    public List<Material> getBlockDrops();

    /**
     * Get various setting that effect tnt in the map.
     * @return The tnt settings.
     */
    public GameTNT getTnt();

    /**
     * Get the various settings that effect bows in the map.
     * @return The bow settings.
     */
    public GameBow getBow();

    /**
     * Get the various settings about chests.
     * @return The chest settings.
     */
    public GameChest getChests();

    public List<Location> getSpawn();
}
