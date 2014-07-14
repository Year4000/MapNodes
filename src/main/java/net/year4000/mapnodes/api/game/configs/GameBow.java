package net.year4000.mapnodes.api.game.configs;

import org.bukkit.entity.EntityType;

public interface GameBow {
    /**
     * Get the entity that is shot from the bow.
     * @return The entity.
     */
    public EntityType getEntity();

    /**
     * The velocity amplifier that effects the entity out of the bow.
     * @return The amplifier amount.
     */
    public double getVelocity();
}
