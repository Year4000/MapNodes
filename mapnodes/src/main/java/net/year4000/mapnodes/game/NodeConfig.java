/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.game;

import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;
import net.year4000.mapnodes.api.exceptions.InvalidJsonException;
import net.year4000.mapnodes.api.game.GameConfig;
import net.year4000.mapnodes.api.utils.Validator;
import net.year4000.mapnodes.api.utils.WorldTime;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.typewrappers.LocationList;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.google.common.base.Preconditions.checkArgument;

/** General game settings. */
public final class NodeConfig implements GameConfig, Validator {
    /** The map's difficulty level. */
    @Since(1.0)
    private Difficulty difficulty = Difficulty.NORMAL;

    /** The time that the map should be locked to. */
    @Since(2.0)
    @SerializedName("time_lock")
    private WorldTime timeLock = new WorldTime(-1);

    /** Should the weather be forced on. */
    @Since(1.0)
    private boolean weather = false;

    /** The Environment of the world. */
    @Since(2.0)
    private World.Environment environment = World.Environment.NORMAL;

    /** The height of the world. */
    @Since(1.0)
    @SerializedName("world_height")
    private int worldHeight = 256;

    /** The resource pack url for this map. */
    @Since(1.0)
    @SerializedName("resource_pack")
    private URL resourcePack = null;

    /** Should chests be cleared on map load. */
    @Since(2.0)
    @SerializedName("clear_chests")
    private boolean clearChests = true;

    /** The area for the spawn. */
    @Since(2.0)
    private LocationList<Location> spawn = new LocationList<>();

    public NodeConfig() {
    }

    @Override
    public void validate() throws InvalidJsonException {
        checkArgument(0 <= worldHeight && worldHeight <= 256, Msg.util("settings.game.worldHeight"));

        checkArgument(spawn.size() > 0, Msg.util("settings.game.spawn"));
    }

    /*//--------------------------------------------//
         Upper Json Settings / Bellow Instance Code
    *///--------------------------------------------//

    /** Get a random spawn, it may not be safe for a player */
    public Location getRandomSpawn() {
        return spawn.get(new Random().nextInt(spawn.size()));
    }

    /** Try and get a safe random spawn or end with a random spawn that may not be safe */
    public Location getSafeRandomSpawn() {
        List<Location> list = new ArrayList<>(spawn);
        Collections.shuffle(list);

        for (Location spawn : list) {
            boolean currentBlock = spawn.getBlock().getType().isTransparent();
            boolean standBlock = spawn.getBlock().getRelative(BlockFace.DOWN).getType().isSolid();
            boolean headBlock = spawn.getBlock().getRelative(BlockFace.UP).getType().isTransparent();

            if (currentBlock && standBlock && headBlock) {
                return spawn;
            }
        }

        return getRandomSpawn();
    }

    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    public WorldTime getTimeLock() {
        return this.timeLock;
    }

    public boolean isWeather() {
        return this.weather;
    }

    public World.Environment getEnvironment() {
        return this.environment;
    }

    public int getWorldHeight() {
        return this.worldHeight;
    }

    public URL getResourcePack() {
        return this.resourcePack;
    }

    public boolean isClearChests() {
        return this.clearChests;
    }

    public LocationList<Location> getSpawn() {
        return this.spawn;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void setTimeLock(WorldTime timeLock) {
        this.timeLock = timeLock;
    }

    public void setWeather(boolean weather) {
        this.weather = weather;
    }

    public void setEnvironment(World.Environment environment) {
        this.environment = environment;
    }

    public void setWorldHeight(int worldHeight) {
        this.worldHeight = worldHeight;
    }

    public void setResourcePack(URL resourcePack) {
        this.resourcePack = resourcePack;
    }

    public void setClearChests(boolean clearChests) {
        this.clearChests = clearChests;
    }

    public void setSpawn(LocationList<Location> spawn) {
        this.spawn = spawn;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof NodeConfig)) return false;
        final NodeConfig other = (NodeConfig) o;
        final Object this$difficulty = this.getDifficulty();
        final Object other$difficulty = other.getDifficulty();
        if (this$difficulty == null ? other$difficulty != null : !this$difficulty.equals(other$difficulty))
            return false;
        final Object this$timeLock = this.getTimeLock();
        final Object other$timeLock = other.getTimeLock();
        if (this$timeLock == null ? other$timeLock != null : !this$timeLock.equals(other$timeLock)) return false;
        if (this.isWeather() != other.isWeather()) return false;
        final Object this$environment = this.getEnvironment();
        final Object other$environment = other.getEnvironment();
        if (this$environment == null ? other$environment != null : !this$environment.equals(other$environment))
            return false;
        if (this.getWorldHeight() != other.getWorldHeight()) return false;
        final Object this$resourcePack = this.getResourcePack();
        final Object other$resourcePack = other.getResourcePack();
        if (this$resourcePack == null ? other$resourcePack != null : !this$resourcePack.equals(other$resourcePack))
            return false;
        if (this.isClearChests() != other.isClearChests()) return false;
        final Object this$spawn = this.getSpawn();
        final Object other$spawn = other.getSpawn();
        if (this$spawn == null ? other$spawn != null : !this$spawn.equals(other$spawn)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $difficulty = this.getDifficulty();
        result = result * PRIME + ($difficulty == null ? 43 : $difficulty.hashCode());
        final Object $timeLock = this.getTimeLock();
        result = result * PRIME + ($timeLock == null ? 43 : $timeLock.hashCode());
        result = result * PRIME + (this.isWeather() ? 79 : 97);
        final Object $environment = this.getEnvironment();
        result = result * PRIME + ($environment == null ? 43 : $environment.hashCode());
        result = result * PRIME + this.getWorldHeight();
        final Object $resourcePack = this.getResourcePack();
        result = result * PRIME + ($resourcePack == null ? 43 : $resourcePack.hashCode());
        result = result * PRIME + (this.isClearChests() ? 79 : 97);
        final Object $spawn = this.getSpawn();
        result = result * PRIME + ($spawn == null ? 43 : $spawn.hashCode());
        return result;
    }

    public String toString() {
        return "net.year4000.mapnodes.game.NodeConfig(difficulty=" + this.getDifficulty() + ", timeLock=" + this.getTimeLock() + ", weather=" + this.isWeather() + ", environment=" + this.getEnvironment() + ", worldHeight=" + this.getWorldHeight() + ", resourcePack=" + this.getResourcePack() + ", clearChests=" + this.isClearChests() + ", spawn=" + this.getSpawn() + ")";
    }
}