package net.year4000.mapnodes.game;

import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.listeners.ListenerBuilder;
import org.bukkit.event.EventHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class NodeModeManager {
    private Map<GameModeInfo, ListenerBuilder> enabledListeners = new HashMap<>();

    /** enable the current listeners for the game mode */
    public void registerListeners(GameMode mode) {
        try {
            GameModeInfo configName = mode.getClass().getAnnotation(GameModeInfo.class);
            ListenerBuilder builder = new ListenerBuilder();

            // If main game mode class has EventHandlers load that instance as a listener
            if (Arrays.asList(mode.getClass().getMethods()).parallelStream().filter(m -> m.getAnnotation(EventHandler.class) != null).count() > 0L) {
                builder.registerInstance(mode);
            }

            builder.addAll(configName.listeners());
            builder.register();
            enabledListeners.put(configName, builder);
        } catch (NullPointerException e) {
            MapNodesPlugin.log(e, false);
        }
    }

    /** disable the current listeners for the game mode */
    public void unregisterListeners(GameMode mode) {
        try {
            GameModeInfo modeInfo = mode.getClass().getAnnotation(GameModeInfo.class);

            enabledListeners.remove(modeInfo).unregister();
        } catch (NullPointerException e) {
            MapNodesPlugin.log(e, false);
        }
    }
}
