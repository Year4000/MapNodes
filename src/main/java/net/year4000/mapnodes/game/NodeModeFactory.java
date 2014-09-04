package net.year4000.mapnodes.game;

import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeConfig;
import net.year4000.mapnodes.api.game.modes.GameModeConfigName;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.messages.Msg;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import java.security.InvalidParameterException;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

public class NodeModeFactory {
    private static NodeModeFactory inst;
    private Set<Class<? extends GameMode>> rawGameModes = new HashSet<>();
    private Map<GameModeInfo, Class<? extends GameMode>> loadedGameModes = new HashMap<>();
    private boolean built = false;

    public static NodeModeFactory get() {
        if (inst == null) {
            inst = new NodeModeFactory();
        }

        return inst;
    }

    public NodeModeFactory add(Class<? extends GameMode> type) {
        rawGameModes.add(type);
        return this;
    }

    /** Builds all the added gamemodes so we can check and register types */
    public void build() {
        rawGameModes.forEach(type -> {
            try {
                GameModeInfo info = type.getAnnotation(GameModeInfo.class);
                loadedGameModes.put(info, type);
            } catch (NullPointerException e) {
                MapNodesPlugin.log(e, false);
            }
        });
        built = true;
    }

    /** get new instance of GameMode from the assigned config */
    public GameMode getFromConfig(Class<? extends GameModeConfig> name) {
        checkArgument(built);

        // loop through all registered game modes and return game mode instance
        for (Map.Entry<GameModeInfo, Class<? extends GameMode>> mode : loadedGameModes.entrySet()) {
            if (mode.getKey().config() == name) {
                try {
                    return mode.getValue().newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    MapNodesPlugin.log(e, false);
                }
            }
        }

        throw new InvalidParameterException(name + " is not a valid game mode type.");
    }

    /** Get the true game mode config type for gson to handle the GameModeConfig interface */
    public Class<? extends GameModeConfig> getGameModeConfig(String name) {
        checkArgument(built);

        // loop through all registered game modes and return game mode instance
        for (GameModeInfo info : loadedGameModes.keySet()) {
            try {
                GameModeConfigName configName = info.config().getAnnotation(GameModeConfigName.class);

                if (configName.value().equals(name.toLowerCase())) {
                    return info.config();
                }
            } catch (NullPointerException e) {
                MapNodesPlugin.log(e, false);
            }
        }

        throw new InvalidParameterException(name + " is not a valid game mode type.");
    }

    /** Get the instance of the game mode */
    public GameMode getGameMode(Class<? extends GameMode> mode) {
        for (GameMode modes : MapNodes.getCurrentGame().getGameModes()) {
            if (modes.getClass() == mode) {
                return modes;
            }
        }

        throw new InvalidParameterException(mode.getCanonicalName() + " is not a valid game mode type.");
    }

    /** enable the current listeners for the game mode */
    public void registerListeners(GameMode mode) {
        PluginManager manager = Bukkit.getPluginManager();
        List<Listener> listeners = new ArrayList<>();

        try {
            GameModeInfo configName = mode.getClass().getAnnotation(GameModeInfo.class);

            for (Class<? extends Listener> listen : configName.listeners()) {
                try {
                    MapNodesPlugin.debug(Msg.util("debug.listener.register", listen.getSimpleName()));
                    Listener listener = listen.newInstance();
                    manager.registerEvents(listener, MapNodesPlugin.getInst());
                    listeners.add(listener);
                    mode.setListeners(listeners);
                } catch (InstantiationException | IllegalAccessException e) {
                    MapNodesPlugin.log(e, false);
                }
            }
        } catch (NullPointerException e) {
            MapNodesPlugin.log(e, false);
        }
    }

    /** disable the current listeners for the game mode */
    public void unregisterListeners(GameMode mode) {
        mode.getListeners().forEach(l -> {
            MapNodesPlugin.debug(Msg.util("debug.listener.unregister", l.getClass().getSimpleName()));
            HandlerList.unregisterAll(l);
        });
    }
}
