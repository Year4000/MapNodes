package net.year4000.mapnodes.listeners;

import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.messages.Msg;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListenerBuilder {
    private static final PluginManager manager = Bukkit.getPluginManager();
    private List<Class<?>> listeners = new ArrayList<>();
    private List<Listener> registered = new ArrayList<>();

    /** Add a class to this listener builder */
    public ListenerBuilder add(Class<?> commandClass) {
        listeners.add(commandClass);
        return this;
    }

    /** Add a class to this listener builder */
    public ListenerBuilder addAll(Class<?>... commandClass) {
        Collections.addAll(listeners, commandClass);

        return this;
    }

    /** Register a listener when the instance is all ready created */
    public void registerInstance(Listener listener) {
        MapNodesPlugin.debug(Msg.util("debug.listener.register", listener.getClass().getSimpleName()));
        manager.registerEvents(listener, MapNodesPlugin.getInst());
        registered.add(listener);
    }

    /** Register the listeners */
    public void register() {
        listeners.forEach(listener -> {
            try {
                MapNodesPlugin.debug(Msg.util("debug.listener.register", listener.getSimpleName()));
                Listener instance = (Listener) listener.newInstance();
                manager.registerEvents(instance, MapNodesPlugin.getInst());
                registered.add(instance);
            } catch (InstantiationException | IllegalAccessException e) {
                MapNodesPlugin.debug(e, true);
            }
        });
    }

    /** Unregister the listeners */
    public void unregister() {
        registered.forEach(listener -> {
            MapNodesPlugin.debug(Msg.util("debug.listener.unregister", listener.getClass().getSimpleName()));
            HandlerList.unregisterAll(listener);
        });
    }
}
