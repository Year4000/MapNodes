package net.year4000.mapnodes.listeners;

import net.year4000.mapnodes.utils.BarAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public final class BarAPIListener implements Listener {
    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        BarAPI.handleTeleport(event.getPlayer(), event.getTo());
    }

    @EventHandler
    public void onDeath(PlayerRespawnEvent event) {
        BarAPI.handleTeleport(event.getPlayer(), event.getRespawnLocation());
    }
}
