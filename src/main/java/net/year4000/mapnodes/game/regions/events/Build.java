package net.year4000.mapnodes.game.regions.events;

import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.game.regions.EventType;
import net.year4000.mapnodes.game.regions.EventTypes;
import net.year4000.mapnodes.game.regions.RegionEvent;
import net.year4000.mapnodes.game.regions.RegionListener;
import net.year4000.mapnodes.game.regions.types.Point;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.mapnodes.utils.typewrappers.MaterialList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@EventType(EventTypes.BUILD)
public class Build extends RegionEvent implements RegionListener {
    private transient List<Integer> gravityEntities = new ArrayList<>();
    private transient List<Block> gravityBlocks = new ArrayList<>();
    private MaterialList<Material> blocks = new MaterialList<>(Arrays.asList(Material.values()));
    private boolean gravity = false;

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (!region.inZone(new Point(event.getBlockPlaced().getLocation().toVector().toBlockVector()))) return;

        GamePlayer player = region.getGame().getPlayer(event.getPlayer());
        Material material = event.getBlockPlaced().getType();

        if (applyToPlayer(player)) {
            if (isAllowSet()) {
                if ((isAllow() && blocks.contains(material)) || (!isAllow() && !blocks.contains(material))) {
                    event.setCancelled(false);
                }
                else if ((isAllow() && !blocks.contains(material)) || (!isAllow() && blocks.contains(material))) {
                    event.setCancelled(true);
                }
            }

            // Make the block have gravity when placed
            if (gravity && blocks.contains(material) && event.getBlockPlaced().getRelative(BlockFace.DOWN).isEmpty()) {
                FallingBlock block = event.getBlock().getWorld().spawnFallingBlock(event.getBlockPlaced().getLocation(), material, event.getBlockPlaced().getData());
                block.setDropItem(false);
                gravityEntities.add(block.getEntityId());
                event.getBlock().setType(Material.AIR);
            }
            else if (gravity && blocks.contains(material)) {
                gravityBlocks.add(event.getBlock());
            }

            runGlobalEventTasks(player);
            runGlobalEventTasks(event.getBlock().getLocation());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockLand(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof org.bukkit.entity.FallingBlock) {
            if (event.getBlock().getType() == Material.AIR && gravityEntities.remove((Integer) event.getEntity().getEntityId())) {
                SchedulerUtil.runSync(() -> {
                    Location loc = event.getBlock().getLocation();
                    gravityBlocks.add(loc.getBlock());
                });
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        if (gravityBlocks.remove(event.getBlock().getRelative(BlockFace.UP))) {
            Block block = event.getBlock().getRelative(BlockFace.UP);
            FallingBlock fallingBlock = event.getBlock().getWorld().spawnFallingBlock(block.getLocation(), block.getType(), block.getData());
            fallingBlock.setDropItem(false);
            gravityEntities.add(fallingBlock.getEntityId());
            event.getBlock().setType(Material.AIR);
        }
    }
}
