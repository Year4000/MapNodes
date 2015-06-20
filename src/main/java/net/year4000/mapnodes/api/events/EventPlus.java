/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.api.events;

import net.year4000.mapnodes.api.MapNodes;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class EventPlus extends Event {
    private static final HandlerList handlers = new HandlerList();

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
}
