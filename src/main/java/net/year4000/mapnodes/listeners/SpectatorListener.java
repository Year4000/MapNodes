package net.year4000.mapnodes.listeners;

import net.year4000.mapnodes.MapNodes;
import net.year4000.mapnodes.game.GameManager;
import net.year4000.mapnodes.game.GamePlayer;
import net.year4000.mapnodes.game.GameStage;
import net.year4000.mapnodes.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

@SuppressWarnings("unused")
/** Controls the aspects of the game that for the Spectator. */
public class SpectatorListener implements Listener {

    /** Register its self. */
    public SpectatorListener() {
        Bukkit.getPluginManager().registerEvents(this, MapNodes.getInst());
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        GameManager gm = WorldManager.get().getCurrentGame();
        GamePlayer gPlayer = gm.getPlayer(event.getPlayer());

        event.setCancelled(gPlayer.isSpecatator() || !GameStage.isPlaying() || !gPlayer.isHasPlayed());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        GameManager gm = WorldManager.get().getCurrentGame();
        GamePlayer gPlayer = gm.getPlayer(event.getPlayer());

        event.setCancelled(gPlayer.isSpecatator() || !GameStage.isPlaying() || !gPlayer.isHasPlayed());
    }

    @EventHandler
    public void onIvnInteract(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        GameManager gm = WorldManager.get().getCurrentGame();
        GamePlayer gPlayer = gm.getPlayer((Player)event.getWhoClicked());

        event.setCancelled(gPlayer.isSpecatator() || !GameStage.isPlaying() || !gPlayer.isHasPlayed());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;

        GameManager gm = WorldManager.get().getCurrentGame();
        GamePlayer gPlayer = gm.getPlayer((Player)event.getDamager());

        event.setCancelled(gPlayer.isSpecatator() || !GameStage.isPlaying() || !gPlayer.isHasPlayed());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPickUp(PlayerPickupItemEvent event) {
        GameManager gm = WorldManager.get().getCurrentGame();
        GamePlayer gPlayer = gm.getPlayer(event.getPlayer());

        event.setCancelled(gPlayer.isSpecatator() || !GameStage.isPlaying() || !gPlayer.isHasPlayed());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent event) {
        // If not a player don't check
        if (!(event.getEntity() instanceof Player)) return;

        GameManager gm = WorldManager.get().getCurrentGame();
        GamePlayer gPlayer = gm.getPlayer((Player)event.getEntity());

        if (gPlayer.isSpecatator() || !GameStage.isPlaying() || !gPlayer.isHasPlayed()) {
            // If the damage is void reset player
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                gPlayer.respawn();
                gPlayer.getPlayer().teleport(gPlayer.getTeam().getRandomSpawn());
                return;
            }

            event.setCancelled(true);
        }
    }
}
