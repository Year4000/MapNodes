package net.year4000.mapnodes.listeners;

import net.year4000.mapnodes.MapNodes;
import net.year4000.mapnodes.configs.Messages;
import net.year4000.mapnodes.game.GameManager;
import net.year4000.mapnodes.game.GameMap;
import net.year4000.mapnodes.utils.ChestUtil;
import net.year4000.mapnodes.world.WorldManager;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;

/** Controls how the game should behave base on the json file. */
@SuppressWarnings("unused")
public class MapConfigListener implements Listener {
    private static List<Vector> chests = new ArrayList<>();
    private static Random rand = new Random(System.currentTimeMillis());

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
            // If not set clear all
            if (gm.getMap().getEnabledPlayerDrops().size() < 1) {
                event.getDrops().clear();
                return;
            }

            // Enable specific drops
            for (int i = 0; i < event.getDrops().size(); i++) {
                if (!gm.getMap().getEnabledPlayerDrops().contains(event.getDrops().get(i).getType())) {
                    event.getDrops().remove(i);
                    event.getDrops().add(i, null);
                }
            }
        }
        else {
            event.getDrops().clear();
        }
    }

    /** The world height cap. */
    @EventHandler(priority=EventPriority.HIGH)
    public void onHeight(BlockPlaceEvent event) {
        int height = WorldManager.get().getCurrentGame().getMap().getWorldHeight();

        if (height > 0) {
            int y = event.getBlockPlaced().getY();

            if (y >= height) {
                event.getPlayer().sendMessage(String.format(
                    Messages.get(event.getPlayer().getLocale(), "game-height-max"),
                    height
                ));
                event.setCancelled(true);
            }
        }
    }

    /** Spawn random items into empty chests. */
    @EventHandler
    public void onChest(PlayerInteractEvent event) {
        GameManager gm =  WorldManager.get().getCurrentGame();
        if (gm.getMap().getChestItems().size() == 0) return;

        Player player = event.getPlayer();
        boolean rightClick = event.getAction() == Action.RIGHT_CLICK_BLOCK;

        if (rightClick && event.getClickedBlock().getState() instanceof Chest) {
            Inventory chest = ((Chest)event.getClickedBlock().getState()).getInventory();
            Vector location = event.getClickedBlock().getLocation().toVector();

            //System.out.println(location);
            // If empty add to array list and add items to chest
            if (ChestUtil.isEmpty(chest) && !chests.contains(location)) {
                chests.add(location);
                ItemStack[] chestContents = new ItemStack[chest.getSize()];

                for (int i = 0; i < gm.getMap().getAmount(); i++) {
                    if (gm.getMap().isScatter()) {
                        int itemIndex = Math.abs(rand.nextInt(gm.getMap().getChestItems().size()));
                        int scatterIndex = Math.abs(rand.nextInt(chest.getSize()));
                        chestContents[scatterIndex] = gm.getMap().getChestItems().get(itemIndex);
                    }
                    else {
                        int itemIndex = Math.abs(rand.nextInt(gm.getMap().getChestItems().size()));
                        chestContents[i] = gm.getMap().getChestItems().get(itemIndex);
                    }
                }

                Bukkit.getScheduler().runTask(MapNodes.getInst(), () -> chest.setContents(chestContents));
            }
        }
    }
    /** Spawn random items into empty chests. */
    @EventHandler(priority=EventPriority.MONITOR)
    public void onChest(BlockPlaceEvent event) {
        GameManager gm =  WorldManager.get().getCurrentGame();
        if (gm.getMap().getChestItems().size() == 0) return;

        if (event.getBlock().getState() instanceof Chest) {
            chests.add(event.getBlock().getLocation().toVector());
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
