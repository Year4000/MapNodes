package net.year4000.mapnodes.listeners;

import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

public class ListenerBuilder {
    private List<Class<?>> listeners = new ArrayList<>();
    private List<Listener> registered = new ArrayList<>();

    /** Add a class to this listener builder */
    public ListenerBuilder add(Class<?> commandClass) {
        listeners.add(commandClass);
        return this;
    }

    /** Register the listeners */
    public void register() {
        PluginManager manager = Bukkit.getPluginManager();
        listeners.forEach(listener -> {
            try {
                LogUtil.debug(Msg.util("debug.listener.register"), listener.getSimpleName());
                Listener instance = (Listener) listener.newInstance();
                manager.registerEvents(instance, MapNodesPlugin.getInst());
                registered.add(instance);
            } catch (InstantiationException | IllegalAccessException e) {
                LogUtil.debug(e, true);
            }
        });
    }

    /** Unregister the listeners */
    public void unregister() {
        registered.forEach(HandlerList::unregisterAll);
    }
}
