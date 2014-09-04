package net.year4000.mapnodes.api.game.modes;

import org.bukkit.event.Listener;

import java.util.List;
import java.util.Set;

public interface GameMode {
    public GameModeConfig getConfig();

    public void setConfig(GameModeConfig config);

    public List<Listener> getListeners();

    public void setListeners(List<Listener> listeners);
}
