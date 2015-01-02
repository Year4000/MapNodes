package net.year4000.mapnodes.api;

import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.utilities.LogUtil;
import org.bukkit.World;

public interface Plugin {
    /** Get the current game */
    public GameManager getCurrentGame();

    /** Get the current game world */
    public World getCurrentWorld();

    /** Get the log util to log things */
    public LogUtil getLogUtil();
}
