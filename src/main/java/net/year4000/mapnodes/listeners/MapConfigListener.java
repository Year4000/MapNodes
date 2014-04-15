package net.year4000.mapnodes.listeners;

import net.year4000.mapnodes.MapNodes;
import net.year4000.mapnodes.game.GameManager;
import net.year4000.mapnodes.game.GameMap;
import net.year4000.mapnodes.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/** Controls how the game should behave base on the json file. */
@SuppressWarnings("unused")
public class MapConfigListener implements Listener {
    /** Register this class as an event listener. */
    public MapConfigListener() {
        Bukkit.getPluginManager().registerEvents(this, MapNodes.getInst());
    }

    /** Disallow entity damage based on map.json */
    @EventHandler(priority=EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event) {
        GameMap map = WorldManager.get().getCurrentGame().getMap();
        event.setCancelled(map.getNoDamage().contains(event.getCause()));
    }

    /** Allow creature spawn based on the map.json */
    @EventHandler(priority=EventPriority.HIGH)
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        GameMap map = WorldManager.get().getCurrentGame().getMap();
        event.setCancelled(!map.getEnabledMobs().contains(event.getEntityType()));
    }

    /** Is the map destructible based on the config. */
    @EventHandler(priority=EventPriority.HIGH)
    public void onDestructible(BlockBreakEvent event) {
        GameManager gm = WorldManager.get().getCurrentGame();
        event.setCancelled(!gm.getMap().isDestructible());
    }

    /** If a tnt is place should be ignite it */
    @EventHandler(priority=EventPriority.MONITOR)
    public void onTnt(BlockPlaceEvent event) {
        if (event.getBlock().getType() != Material.TNT) return;

        if (WorldManager.get().getCurrentGame().getMap().isInstantTNT()) {
            event.getBlock().setType(Material.AIR);

            // Create the tnt to look like it
            TNTPrimed tnt = event.getPlayer().getWorld().spawn(
                event.getBlock().getLocation().add(0.5, 0.5, 0.5), // center it
                TNTPrimed.class
            );
            tnt.setFuseTicks(4 * 20);
            tnt.setYield(0);

            // Run the explosion later
            Bukkit.getScheduler().runTaskLater(MapNodes.getInst(), () -> {
                event.getBlock().getWorld().createExplosion(
                    event.getBlock().getLocation().add(0.5, 0.5, 0.5), // center it
                    4 // the strength of tnt
                );
            }, 4 * 20);
        }
    }

    /** Set the yield and blocks of the tnt. */
    @EventHandler(priority=EventPriority.HIGH)
    public void onTnt(EntityExplodeEvent event) {
        if (event.getEntityType() == EntityType.PRIMED_TNT) {
            event.setYield(WorldManager.get().getCurrentGame().getMap().getTntYield());

            if (!WorldManager.get().getCurrentGame().getMap().isTntBlockDamage()) {
                event.blockList().clear();
            }
        }
    }

    /** Set if the explosion should break blocks. */
    @EventHandler(priority=EventPriority.HIGH)
    public void onTnt(ExplosionPrimeEvent event) {
        // TODO Find what this does
    }

    /** Only drop the items that are in the settings. */
    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerInvDrop(PlayerDeathEvent event) {
        GameManager gm = WorldManager.get().getCurrentGame();

        if (!gm.getPlayer(event.getEntity()).isSpecatator()) {
            if (gm.getMap().getEnabledPlayerDrops().size() < 1) {
                event.getDrops().clear();
                return;
            }

            for (int i = 0; i < event.getDrops().size(); i++) {
                for (ItemStack keep : gm.getMap().getEnabledPlayerDrops()) {
                    if (event.getDrops().get(i).getType() != keep.getType()) {
                        event.getDrops().remove(i);
                        event.getDrops().add(i, new ItemStack(Material.AIR));
                    }
                }
            }
        }
        else {
            event.getDrops().clear();
        }
    }

    /** Controls how the bow is used. */
    // TODO: BETTER BOW LOGIC
    /*
    @EventHandler(priority=EventPriority.HIGH)
    public void onShoot(EntityShootBowEvent event) {
        GameManager gm = WorldManager.get().getCurrentGame();


        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Entity arrow = event.getProjectile();

            Vector direction = arrow.getLocation().getDirection().normalize();
            Location spawn = arrow.getLocation();
            spawn.add(direction);
            spawn.setY(arrow.getLocation().getY());

            Entity entity = arrow.getWorld().spawnEntity(
                spawn,
                gm.getMap().getBowEntity()
            );

            entity.setVelocity(arrow.getVelocity().multiply(gm.getMap().getBowVelocity()));
            event.setProjectile(entity);
        }
    }
    */
}
