package net.year4000.mapnodes.clocks;

import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.configs.MainConfig;
import net.year4000.mapnodes.game.GameManager;
import net.year4000.mapnodes.game.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;

public class WorldClock implements Runnable {
    public WorldClock() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(
            MapNodesPlugin.getInst(),
            this,
            20,
            MainConfig.get().getWorldLockDelay()
        );
    }

    @Override
    public void run() {
        GameManager gm = WorldManager.get().getCurrentGame();
        World world = gm.getWorld();

        world.setDifficulty(numberToDifficulty(gm.getMap().getDifficulty()));
        world.setStorm(gm.getMap().isForceWeather());
        if (gm.getMap().getWorldLock() != -1)
            world.setTime(gm.getMap().getWorldLock());
    }

    /** Map the difficulty level number to the enum. */
    private Difficulty numberToDifficulty(int number) {
        switch (number) {
            case 0: return Difficulty.PEACEFUL;
            case 1: return Difficulty.EASY;
            case 2: return Difficulty.NORMAL;
            case 3: return Difficulty.HARD;
        }
        return WorldManager.get().getCurrentGame().getWorld().getDifficulty();
    }
}
