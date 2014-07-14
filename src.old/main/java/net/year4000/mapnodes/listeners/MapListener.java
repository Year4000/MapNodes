package net.year4000.mapnodes.listeners;

import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.game.GameStage;
import net.year4000.mapnodes.game.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;

/** When the map is not playing disable al events in that world. */
@SuppressWarnings("unused")
public class MapListener implements Listener {
    public MapListener() {
        Bukkit.getPluginManager().registerEvents(this, MapNodesPlugin.getInst());
    }

    /** Return true | false if the map is running. */
    private boolean isMapPlaying(World world) {
        boolean playing = WorldManager.get().getCurrentGame().getStage() == GameStage.PLAYING;
        boolean worldSame = WorldManager.get().getCurrentGame().getWorld() == world;
        return !(playing && worldSame);
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

}