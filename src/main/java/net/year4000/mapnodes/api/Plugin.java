package net.year4000.mapnodes.api;

import net.year4000.mapnodes.api.game.GameManager;
import org.bukkit.World;

public interface Plugin {
    /**
     * Get the current game.
     *
     * @return The current game.
     */
    public GameManager getCurrentGame();

    /**
     * Get the current game world.
     *
     * @return The current game world.
     */
    public World getCurrentWorld();
}
