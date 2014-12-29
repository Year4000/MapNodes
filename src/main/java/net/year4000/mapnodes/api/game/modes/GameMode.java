package net.year4000.mapnodes.api.game.modes;

import org.bukkit.event.Listener;

public interface GameMode extends Listener {
    public <T extends GameModeConfig> T getConfig();

    public void setConfig(GameModeConfig config);

    public GameModeConfig getRawConfig();
}
