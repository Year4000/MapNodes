package net.year4000.mapnodes.game;

import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.year4000.mapnodes.api.game.GameConfig;
import net.year4000.mapnodes.exceptions.InvalidJsonException;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Validator;
import net.year4000.mapnodes.utils.typewrappers.LocationList;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.google.common.base.Preconditions.checkArgument;

@Data
@NoArgsConstructor
/** General game settings. */
public final class NodeConfig implements GameConfig, Validator {
    /** The map's difficulty level. */
    @Since(1.0)
    private int difficulty = 3; // todo allow support for names easy, normal, hard

    /** The time that the map should be locked to. */
    @Since(1.0)
    @SerializedName("time_lock")
    private long timeLock = -1; // todo allow for name's ex dawn, dusk, day, night, midnight, midday

    /** Should the weather be forced on. */
    @Since(1.0)
    private boolean weather = false;

    /** The Environment of the world. */
    @Since(1.0)
    private World.Environment environment = World.Environment.NORMAL; //todo custom deserializer

    /** The height of the world. */
    @Since(1.0)
    @SerializedName("world_height")
    private int worldHeight = 256;

    /** The resource pack url for this map. */
    @Since(1.0)
    @SerializedName("resource_pack")
    private URL resourcePack = null;

    /** The area for the spawn. */
    @Since(1.0)
    private LocationList<Location> spawn = new LocationList<>();

    @Override
    public void validate() throws InvalidJsonException {
        checkArgument(0 <= difficulty && difficulty <= 3, Msg.util("settings.game.difficulty"));

        checkArgument(-1 <= timeLock && timeLock <= 23000, Msg.util("settings.game.timeLock"));

        checkArgument(0 <= worldHeight && worldHeight <= 256, Msg.util("settings.game.worldHeight"));

        checkArgument(spawn.size() > 0, Msg.util("settings.game.spawn"));
    }

    /*//--------------------------------------------//
         Upper Json Settings / Bellow Instance Code
    *///--------------------------------------------//

    /** Get a random spawn, it may not be safe for a player */
    public Location getRandomSpawn() {
        return  spawn.get(new Random().nextInt(spawn.size()));
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
}