package net.year4000.mapnodes.api.game.configs;

public interface GameTNT {
    /**
     * Is the tnt activated when placed.
     * @return The state to allow instant tnt.
     */
    public boolean isInstant();

    /**
     * Is the tnt going to break blocks.
     * @return The state to allow block damage from tnt.
     */
    public boolean isBlockDamage();

    /**
     * The percent of blocks that is allowed to drop from the tnt.
     * @return The percent.
     */
    public int getDrops();
}
