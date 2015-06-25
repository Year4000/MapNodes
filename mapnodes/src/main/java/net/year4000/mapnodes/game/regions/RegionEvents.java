/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.game.regions;

import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.api.game.regions.EventTypes;
import net.year4000.mapnodes.api.game.regions.RegionListener;
import net.year4000.mapnodes.game.NodeRegion;
import net.year4000.mapnodes.messages.Msg;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

public class RegionEvents {
    private List<RegionListener> events = new ArrayList<>();
    private List<EventTypes> enabledTypes = new ArrayList<>();

    public void addEvent(RegionListener listener, EventTypes eventType) {
        events.add(listener);
        enabledTypes.add(eventType);
    }

    public void registerEvents() {
        events.forEach(listener -> {
            MapNodesPlugin.debug(Msg.util("debug.listener.register", listener.getClass().getSimpleName()));
            Bukkit.getPluginManager().registerEvents(listener, MapNodesPlugin.getInst());
        });
    }

    public void unregisterEvents() {
        events.forEach(listener -> {
            MapNodesPlugin.debug(Msg.util("debug.listener.unregister", listener.getClass().getSimpleName()));
            HandlerList.unregisterAll(listener);
        });
    }

    /** Assign region to listener events */
    public void assignRegion(NodeRegion region) {
        events.forEach(event -> event.setRegion(region));
    }
}
