package net.year4000.mapnodes.utils.typewrappers;

import net.year4000.mapnodes.api.MapNodes;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class LocationList<T> extends ArrayList<T> implements List<T> {
    /** Get a random spawn, it may not be safe for a player */
    public Location getRandomSpawn() {
        return size() == 0 ? MapNodes.getCurrentWorld().getSpawnLocation() : (Location) get(new Random().nextInt(size()));
    }

    /** Try and get a safe random spawn or end with a random spawn that may not be safe */
    public Location getSafeRandomSpawn() {
        List<T> list = (List) this.clone();
        Collections.shuffle(list);

        for (T template : list) {
            Location spawn = (Location) template;
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
