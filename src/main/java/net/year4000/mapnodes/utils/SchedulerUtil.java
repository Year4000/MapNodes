package net.year4000.mapnodes.utils;

import net.year4000.mapnodes.MapNodesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public class SchedulerUtil {
    private static MapNodesPlugin plugin = MapNodesPlugin.getInst();
    private static BukkitScheduler scheduler = Bukkit.getScheduler();

    /** Run a task in sync with a delay */
    public static BukkitTask runSync(Runnable task, long delay) {
        return scheduler.runTaskLater(plugin, task, delay);
    }

    /** Run a task in sync */
    public static BukkitTask runSync(Runnable task) {
        return scheduler.runTaskLater(plugin, task, 0);
    }

    /** Run a task Async with a delay */
    public static BukkitTask runAsync(Runnable task, long delay) {
        return scheduler.runTaskLaterAsynchronously(plugin, task, delay);
    }

    /** Run a task Async */
    public static BukkitTask runAsync(Runnable task) {
        return scheduler.runTaskLaterAsynchronously(plugin, task, 0);
    }

    /** Schedule a repeating task sync */
    public static BukkitTask repeatSync(Runnable task, long delay) {
        return scheduler.runTaskTimer(plugin, task, 0, delay);
    }

    /** Schedule a repeating task sync with a delay */
    public static BukkitTask repeatSync(Runnable task, long delay, long period) {
        return scheduler.runTaskTimer(plugin, task, period, delay);
    }
    /** Schedule a repeating task async */
    public static BukkitTask repeatAsync(Runnable task, long delay) {
        return scheduler.runTaskTimerAsynchronously(plugin, task, 0, delay);
    }

    /** Schedule a repeating task async with a delay */
    public static BukkitTask repeatAsync(Runnable task, long delay, long period) {
        return scheduler.runTaskTimerAsynchronously(plugin, task, period, delay);
    }
}
