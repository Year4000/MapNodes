/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.api.events;

import com.google.common.collect.Lists;
import net.year4000.mapnodes.api.MapNodes;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public abstract class EventPlus extends Event {
    private static final HandlerList handlers = new HandlerList();
    protected List<Runnable> postEventTasks = Lists.newCopyOnWriteArrayList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    /** Call the event */
    public void call() {
        try {
            Bukkit.getPluginManager().callEvent(this);
        }
        catch (Exception e) {
            MapNodes.getLogUtil().debug(e, false);
        }
    }

    /** Run all post event tasks */
    public void runPostEvents() {
        postEventTasks.forEach(runnable -> {
            try {
                runnable.run();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /** Add a task to run after the event has processed */
    public void addPostEvent(Runnable task) {
        postEventTasks.add(task);
    }
}
