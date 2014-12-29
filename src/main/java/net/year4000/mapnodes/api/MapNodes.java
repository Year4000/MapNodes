package net.year4000.mapnodes.api;

import net.year4000.mapnodes.api.game.GameManager;
import org.bukkit.World;

public final class MapNodes {
    private static Plugin inst = null;

    public static void init(Plugin inst) {
        if (MapNodes.inst == null) {
            MapNodes.inst = inst;
        }
    }

    /**
     * Get the current game.
     *
     * @return The current game.
     */
    public static GameManager getCurrentGame() {
        return inst.getCurrentGame();
    }

    /**
     * Get the current game world.
     *
     * @return The current game world.
     */
    public static World getCurrentWorld() {
        return inst.getCurrentWorld();
    }
}
