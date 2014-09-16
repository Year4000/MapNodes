package net.year4000.mapnodes.game.regions;

import net.year4000.mapnodes.MapNodesPlugin;
import org.bukkit.event.Listener;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

public final class EventManager {
    private static EventManager inst;
    private Set<Class<? extends Listener>> rawEventTypes = new HashSet<>();
    private Map<EventType, Class<? extends Listener>> eventTypes = new HashMap<>();
    private boolean built = false;

    public static EventManager get() {
        if (inst == null) {
            inst = new EventManager();
        }

        return inst;
    }

    public EventManager add(Class<? extends Listener> type) {
        rawEventTypes.add(type);
        return this;
    }

    public void build() {
        rawEventTypes.forEach(type -> {
            try {
                EventType typeName = type.getAnnotation(EventType.class);
                eventTypes.put(typeName, type);
            } catch (Exception e) {
                MapNodesPlugin.log(e, false);
            }
        });
        built = true;
    }

    public Class<? extends Listener> getRegionType(String name) {
        checkArgument(built); // Make sure we are built

        for (EventType type : eventTypes.keySet()) {
            if (type.value().isType(name)) {
                return eventTypes.get(type);
            }
        }

        throw new InvalidParameterException(name + " is not a valid region type.");
    }
}
