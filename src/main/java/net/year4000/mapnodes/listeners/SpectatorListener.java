package net.year4000.mapnodes.listeners;

import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.game.NodeGame;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

public class SpectatorListener implements Listener {
    /** Stop the event if the player is not playing the game */
    private void stopEvent(Cancellable event, Player player) {
        if (!MapNodes.getCurrentGame().getPlayer(player).isPlaying()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onDrop(PlayerDropItemEvent event) {
        stopEvent(event, event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onInteract(PlayerInteractEvent event) {
        stopEvent(event, event.getPlayer());

    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onInteract(EntityInteractEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        stopEvent(event, (Player) event.getEntity());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onIvnInteract(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        stopEvent(event, (Player) event.getWhoClicked());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;

        stopEvent(event, (Player) event.getDamager());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPickUp(PlayerPickupItemEvent event) {
        stopEvent(event, event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPickUp(PlayerPickupExperienceEvent event) {
        stopEvent(event, event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onDamage(EntityDamageEvent event) {
        // If not a player don't check
        if (!(event.getEntity() instanceof Player)) return;

        // If not playing send player back to spawn
        if (!MapNodes.getCurrentGame().getPlayer((Player) event.getEntity()).isPlaying()) {
            // If the damage is void reset player
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                event.getEntity().teleport(((NodeGame) MapNodes.getCurrentGame()).getConfig().getSafeRandomSpawn());
            }

            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onMove(PlayerMoveEvent event) {
        // Help the spectator ignore side effects
        if (!MapNodes.getCurrentGame().getPlayer(event.getPlayer()).isPlaying()) {
            event.getPlayer().setFireTicks(0);
            event.getPlayer().setFoodLevel(20);
        }
    }
}
