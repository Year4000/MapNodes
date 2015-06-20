/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.listeners;

import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.api.MapNodes;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

@EqualsAndHashCode
public final class WorldListener implements Listener {
    /** Return true | false if the map is running. */
    private boolean isMapPlaying(World world) {
        return !(MapNodes.getCurrentWorld() == world && MapNodes.getCurrentGame().getStage().isPlaying());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(BlockBreakEvent event) {
        event.setCancelled(isMapPlaying(event.getBlock().getWorld()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(BlockPlaceEvent event) {
        event.setCancelled(isMapPlaying(event.getBlock().getWorld()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(BlockDamageEvent event) {
        event.setCancelled(isMapPlaying(event.getBlock().getWorld()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(BlockDispenseEvent event) {
        event.setCancelled(isMapPlaying(event.getBlock().getWorld()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(EntityExplodeEvent event) {
        event.setCancelled(isMapPlaying(event.getLocation().getWorld()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(BlockFadeEvent event) {
        event.setCancelled(isMapPlaying(event.getBlock().getWorld()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(BlockBurnEvent event) {
        event.setCancelled(isMapPlaying(event.getBlock().getWorld()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(BlockGrowEvent event) {
        event.setCancelled(isMapPlaying(event.getBlock().getWorld()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(BlockFormEvent event) {
        event.setCancelled(isMapPlaying(event.getBlock().getWorld()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(BlockPistonExtendEvent event) {
        event.setCancelled(isMapPlaying(event.getBlock().getWorld()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(BlockPistonRetractEvent event) {
        event.setCancelled(isMapPlaying(event.getBlock().getWorld()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(BlockFromToEvent event) {
        event.setCancelled(isMapPlaying(event.getBlock().getWorld()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(BlockIgniteEvent event) {
        event.setCancelled(isMapPlaying(event.getBlock().getWorld()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(BlockPhysicsEvent event) {
        event.setCancelled(isMapPlaying(event.getBlock().getWorld()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(BlockSpreadEvent event) {
        event.setCancelled(isMapPlaying(event.getBlock().getWorld()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(EntityBlockFormEvent event) {
        event.setCancelled(isMapPlaying(event.getBlock().getWorld()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMapPlaying(CreatureSpawnEvent event) {
        event.setCancelled(isMapPlaying(event.getEntity().getWorld()));
    }
}
