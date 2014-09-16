package net.year4000.mapnodes.listeners;

import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.GameRegion;
import net.year4000.mapnodes.game.NodeRegion;
import net.year4000.mapnodes.game.regions.types.Point;
import net.year4000.mapnodes.utils.ChestUtil;
import net.year4000.mapnodes.utils.SchedulerUtil;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RegionListener implements Listener {
    /** Disable movement based on regions */
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (MapNodes.getCurrentGame().getRegions().size() == 0) return;

        Vector v = event.getTo().toVector();

        // Skip check if not a new block
        if (event.getFrom().toVector().toBlockVector().equals(v.toBlockVector())) return;

        Point point = new Point(v.getBlockX(), v.getBlockY(), v.getBlockZ());

        List<GameRegion> regions = NodeRegion.getRegions(point);

        if (regions.size() == 0) return;

        NodeRegion region = (NodeRegion) regions.get(0);
        Player player = event.getPlayer();

        // Should we deny entrance
        if (region.getFlags().getEnter() != null && NodeRegion.applyToPlayer(player, region)) {
            player.teleport(event.getFrom());

            NodeRegion.sendDenyMessage(player, region, "region.deny.enter");
        }
        // Should we deny exit
        else if (region.getFlags().getExit() != null && NodeRegion.applyToPlayer(player, region)) {
            player.teleport(event.getFrom());

            NodeRegion.sendDenyMessage(player, region, "region.deny.exit");
        }
    }

    /** Disable block placing based on regions */
    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        if (MapNodes.getCurrentGame().getRegions().size() == 0) return;

        Vector v = event.getBlockPlaced().getLocation().toVector();
        Point point = new Point(v.getBlockX(), v.getBlockY(), v.getBlockZ());

        List<GameRegion> regions = NodeRegion.getRegions(point);

        if (regions.size() == 0 ) return;

        NodeRegion region = (NodeRegion) regions.get(0);
        Player player = event.getPlayer();

        if (region.getFlags().getBuild() == null && !NodeRegion.applyToPlayer(player, region)) return;

        event.setCancelled(region.getFlags().getBuild());
        NodeRegion.sendDenyMessage(player, region, "region.deny.place");
    }

    /** Disable block break based on regions */
    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        if (MapNodes.getCurrentGame().getRegions().size() == 0) return;

        Vector v = event.getBlock().getLocation().toVector();
        Point point = new Point(v.getBlockX(), v.getBlockY(), v.getBlockZ());

        List<GameRegion> regions = NodeRegion.getRegions(point);

        if (regions.size() == 0 ) return;

        NodeRegion region = (NodeRegion) regions.get(0);
        Player player = event.getPlayer();

        if (region.getFlags().getDestroy() == null && !NodeRegion.applyToPlayer(player, region)) return;

        event.setCancelled(region.getFlags().getDestroy());
        NodeRegion.sendDenyMessage(player, region, "region.deny.break");
    }

    /** Disable block fade based on regions */
    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        if (MapNodes.getCurrentGame().getRegions().size() == 0) return;

        Vector v = event.getBlock().getLocation().toVector();
        Point point = new Point(v.getBlockX(), v.getBlockY(), v.getBlockZ());

        List<GameRegion> regions = NodeRegion.getRegions(point);

        if (regions.size() == 0 ) return;

        NodeRegion region = (NodeRegion) regions.get(0);

        if (region.getFlags().getBlockFade() == null) return;

        event.setCancelled(region.getFlags().getBlockFade());
    }

    /** Disable liquid flowing based on regions */
    @EventHandler
    public void onFlow(BlockFromToEvent event) {
        if (MapNodes.getCurrentGame().getRegions().size() == 0) return;

        Vector v = event.getBlock().getLocation().toVector();
        Point point = new Point(v.getBlockX(), v.getBlockY(), v.getBlockZ());

        List<GameRegion> regions = NodeRegion.getRegions(point);

        if (regions.size() == 0 ) return;

        NodeRegion region = (NodeRegion) regions.get(0);

        if (region.getFlags().getLiquidFlow() == null) return;

        event.setCancelled(region.getFlags().getLiquidFlow());
    }

    /** Disallow entity damage based on regions */
    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (MapNodes.getCurrentGame().getRegions().size() == 0) return;

        Vector v = event.getEntity().getLocation().toVector();
        Point point = new Point(v.getBlockX(), v.getBlockY(), v.getBlockZ());

        List<GameRegion> regions = NodeRegion.getRegions(point);

        if (regions.size() == 0 ) return;

        NodeRegion region = (NodeRegion) regions.get(0);

        if (region.getFlags().getNoDamage() == null) return;

        event.setCancelled(region.getFlags().getNoDamage().contains(event.getCause()));
    }

    /** Disallow creature spawning based on regions */
    @EventHandler(ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (MapNodes.getCurrentGame().getRegions().size() == 0) return;

        Vector v = event.getEntity().getLocation().toVector();
        Point point = new Point(v.getBlockX(), v.getBlockY(), v.getBlockZ());

        List<GameRegion> regions = NodeRegion.getRegions(point);

        if (regions.size() == 0 ) return;

        NodeRegion region = (NodeRegion) regions.get(0);

        if (region.getFlags().getEnabledMobs() == null) return;

        event.setCancelled(region.getFlags().getEnabledMobs().contains(event.getEntityType()));
    }

    /** Player drops only some items is specific in regions */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerInvDrop(PlayerDeathEvent event) {
        if (MapNodes.getCurrentGame().getRegions().size() == 0) return;

        Vector v = event.getEntity().getLocation().toVector();
        Point point = new Point(v.getBlockX(), v.getBlockY(), v.getBlockZ());

        List<GameRegion> regions = NodeRegion.getRegions(point);

        if (regions.size() == 0 ) return;

        NodeRegion region = (NodeRegion) regions.get(0);

        if (region.getFlags().getPlayerDrops() == null) return;

        // Enable specific drops
        for (int i = 0; i < event.getDrops().size(); i++) {
            if (!region.getFlags().getPlayerDrops().contains(event.getDrops().get(i).getType())) {
                event.getDrops().remove(i);
                event.getDrops().add(i, null);
            }
        }
    }

    // TNT Listeners //

    /** If a tnt is place should be ignite it */
    @EventHandler(ignoreCancelled = true)
    public void onTnt(BlockPlaceEvent event) {
        if (event.getBlock().getType() != Material.TNT) return;

        if (MapNodes.getCurrentGame().getRegions().size() == 0) return;

        Vector v = event.getBlock().getLocation().toVector();
        Point point = new Point(v.getBlockX(), v.getBlockY(), v.getBlockZ());

        List<GameRegion> regions = NodeRegion.getRegions(point);

        if (regions.size() == 0 ) return;

        NodeRegion region = (NodeRegion) regions.get(0);

        if (region.getFlags().getTnt() == null || !region.getFlags().getTnt().isInstant()) return;

        event.getBlock().setType(Material.AIR);

        // Create the tnt to look like it
        TNTPrimed tnt = event.getPlayer().getWorld().spawn(
            event.getBlock().getLocation().add(0.5, 0.5, 0.5), // center it
            TNTPrimed.class
        );
        tnt.setFuseTicks(4 * 20);
        tnt.setYield(0);

        // Run the explosion later
        SchedulerUtil.runSync(() -> {
            event.getBlock().getWorld().createExplosion(
                event.getBlock().getLocation().add(0.5, 0.5, 0.5), // center it
                4 // the strength of tnt
            );
        }, 4 * 20);
    }

    /** Set the yield and blocks of the tnt. */
    @EventHandler(priority=EventPriority.HIGH)
    public void onTnt(EntityExplodeEvent event) {
        if (MapNodes.getCurrentGame().getRegions().size() == 0) return;

        Vector v = event.getLocation().toVector();
        Point point = new Point(v.getBlockX(), v.getBlockY(), v.getBlockZ());

        List<GameRegion> regions = NodeRegion.getRegions(point);

        if (regions.size() == 0 ) return;

        NodeRegion region = (NodeRegion) regions.get(0);

        if (region.getFlags().getTnt() == null) return;

        event.setYield(region.getFlags().getTnt().getDrops());

        if (!region.getFlags().getTnt().isBlockDamage()) {
            event.blockList().clear();
        }
    }

    // Chest Listeners //

    public static List<Vector> chests = new ArrayList<>();
    private static final Random rand = new Random((long) Math.sqrt(System.currentTimeMillis()));

    /** Spawn random items into empty chests. */
    @EventHandler
    public void onChest(PlayerInteractEvent event) {
        if (MapNodes.getCurrentGame().getRegions().size() == 0) return;

        Vector v = event.getClickedBlock().getLocation().toVector();
        Point point = new Point(v.getBlockX(), v.getBlockY(), v.getBlockZ());

        List<GameRegion> regions = NodeRegion.getRegions(point);

        if (regions.size() == 0 ) return;

        NodeRegion region = (NodeRegion) regions.get(0);

        if (region.getFlags().getChests() == null) return;
        boolean rightClick = event.getAction() == Action.RIGHT_CLICK_BLOCK;

        if (rightClick && event.getClickedBlock().getState() instanceof Chest) {
            Inventory chest = ((Chest) event.getClickedBlock().getState()).getInventory();
            Vector location = event.getClickedBlock().getLocation().toVector();

            // If empty add to array list and add items to chest
            if (ChestUtil.isEmpty(chest) && !chests.contains(location)) {

                chests.add(location);
                ItemStack[] chestContents = new ItemStack[chest.getSize()];

                for (int i = 0; i < region.getFlags().getChests().getAmount(); i++) {
                    if (region.getFlags().getChests().isScatter()) {
                        int itemIndex = Math.abs(rand.nextInt(region.getFlags().getChests().getItems().size()));
                        int scatterIndex = Math.abs(rand.nextInt(chest.getSize()));
                        chestContents[scatterIndex] = region.getFlags().getChests().getItems().get(itemIndex);
                    }
                    else {
                        int itemIndex = Math.abs(rand.nextInt(region.getFlags().getChests().getItems().size()));
                        chestContents[i] = region.getFlags().getChests().getItems().get(itemIndex);
                    }
                }

                SchedulerUtil.runSync(() -> chest.setContents(chestContents));
            }
        }
    }

    /** Spawn random items into empty chests. */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onChest(BlockPlaceEvent event) {
        if (MapNodes.getCurrentGame().getRegions().size() == 0) return;

        Vector v = event.getBlock().getLocation().toVector();
        Point point = new Point(v.getBlockX(), v.getBlockY(), v.getBlockZ());

        List<GameRegion> regions = NodeRegion.getRegions(point);

        if (regions.size() == 0 ) return;

        NodeRegion region = (NodeRegion) regions.get(0);

        if (region.getFlags().getChests() == null) return;

        if (event.getBlock().getState() instanceof Chest) {
            chests.add(event.getBlock().getLocation().toVector());
        }
    }
}
