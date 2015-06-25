/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.game.regions;

import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.api.game.regions.EventType;
import net.year4000.mapnodes.api.game.regions.RegionListener;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

public final class EventManager {
    private static EventManager inst;
    private Set<Class<? extends RegionListener>> rawEventTypes = new HashSet<>();
    private Map<EventType, Class<? extends RegionListener>> eventTypes = new HashMap<>();
    private boolean built = false;

    public static EventManager get() {
        if (inst == null) {
            inst = new EventManager();
        }

        return inst;
    }

    public EventManager add(Class<? extends RegionListener> type) {
        rawEventTypes.add(type);
        return this;
    }

    public void build() {
        rawEventTypes.forEach(type -> {
            try {
                EventType typeName = type.getAnnotation(EventType.class);
                eventTypes.put(typeName, type);
            }
            catch (Exception e) {
                MapNodesPlugin.log(e, false);
            }
        });
        built = true;
    }

    public Class<? extends RegionListener> getRegionType(String name) {
        checkArgument(built); // Make sure we are built

        for (EventType type : eventTypes.keySet()) {
            if (type.value().isType(name)) {
                return eventTypes.get(type);
            }
        }

        throw new InvalidParameterException(name + " is not a valid region type.");
    }
}
