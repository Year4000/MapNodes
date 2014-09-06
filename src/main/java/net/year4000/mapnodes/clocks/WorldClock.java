package net.year4000.mapnodes.clocks;

import net.year4000.mapnodes.MapNodes;
import net.year4000.mapnodes.configs.MainConfig;
import net.year4000.mapnodes.game.GameManager;
import net.year4000.mapnodes.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;

public class WorldClock implements Runnable {
    public WorldClock() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(
            MapNodes.getInst(),
            this,
            20,
            MainConfig.get().getWorldLockDelay()
        );
    }

    @Override
    public void run() {
        GameManager gm = WorldManager.get().getCurrentGame();
        World world = gm.getWorld();

        world.setStorm(gm.getMap().isForceWeather());
        if (gm.getMap().getWorldLock() != -1)
            world.setTime(gm.getMap().getWorldLock());
    }
}
