package net.year4000.mapnodes.clocks;

import net.year4000.mapnodes.MapNodes;
import net.year4000.mapnodes.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

public class ChunkUnloadClock implements Runnable {
    public ChunkUnloadClock() {
        Bukkit.getScheduler().runTaskLater(MapNodes.getInst(), this, 5 * 20);
    }

    @Override
    public void run() {
        for (World world : Bukkit.getWorlds()) {
            if (WorldManager.get().getCurrentGame().getWorld() == world) continue;

            for (Chunk chunk : world.getLoadedChunks())
                chunk.unload(true);
        }
    }
}
