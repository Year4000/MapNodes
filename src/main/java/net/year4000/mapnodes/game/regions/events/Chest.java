package net.year4000.mapnodes.game.regions.events;

import com.google.common.collect.Iterables;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.game.regions.EventType;
import net.year4000.mapnodes.game.regions.EventTypes;
import net.year4000.mapnodes.game.regions.RegionEvent;
import net.year4000.mapnodes.game.regions.RegionListener;
import net.year4000.mapnodes.game.regions.types.Point;
import net.year4000.mapnodes.utils.ChestUtil;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.mapnodes.utils.typewrappers.ItemStackList;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@EventType(EventTypes.CHEST)
public class Chest extends RegionEvent implements RegionListener {
    private static final Random rand = new Random(System.currentTimeMillis());
    private transient List<BlockVector> chests = new ArrayList<>();
    private ItemStackList<ItemStack> items = new ItemStackList<>();
    private boolean fill = false;
    private int amount = 0;
    private boolean scatter = false;

    /** Spawn random items into empty chests. */
    @EventHandler
    public void onChest(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            BlockVector location = event.getClickedBlock().getLocation().toVector().toBlockVector();

            if (!region.inZone(new Point(location))) return;

            /*items.forEach(i -> MapNodesPlugin.log(i.toString()));
            MapNodesPlugin.log("ITEMS: " + items.size());
            MapNodesPlugin.log("FILL: " + fill);
            MapNodesPlugin.log("AMOUNT: " + amount);
            MapNodesPlugin.log("SCATTER: " + scatter);*/

            final Inventory chest;

            if (event.getClickedBlock().getState() instanceof org.bukkit.block.Chest) {
                chest = ((org.bukkit.block.Chest) event.getClickedBlock().getState()).getInventory();
            }
            else if (event.getClickedBlock().getState() instanceof org.bukkit.block.DoubleChest) {
                chest = ((org.bukkit.block.DoubleChest) event.getClickedBlock().getState()).getInventory();
            }
            else {
                return;
            }

            // If empty add to array list and add items to chest
            if (ChestUtil.isEmpty(chest) && !chests.contains(location)) {
                chests.add(location);
                ItemStack[] chestContents = new ItemStack[chest.getSize()];
                Iterator<ItemStack> itemIterator = Iterables.cycle(items).iterator();

                for (int i = 0; i < (amount > chest.getSize() || fill ? chest.getSize() : amount); i++) {
                    if (scatter) {
                        int itemIndex = Math.abs(rand.nextInt(items.size()));
                        int scatterIndex = Math.abs(rand.nextInt(chest.getSize()));
                        chestContents[scatterIndex] = items.get(itemIndex);
                        // MapNodesPlugin.log("SCATTER");
                    }
                    else if (scatter && fill) {
                        int scatterIndex = Math.abs(rand.nextInt(chest.getSize()));
                        chestContents[scatterIndex] = itemIterator.next();
                        // MapNodesPlugin.log("SCATTER & FILL");
                    }
                    else if (fill) {
                        chestContents[i] = itemIterator.next();
                        // MapNodesPlugin.log("FILL");
                    }
                    else {
                        int itemIndex = Math.abs(rand.nextInt(items.size()));
                        chestContents[i] = items.get(itemIndex);
                        // MapNodesPlugin.log("OTHER");
                    }
                }

                // MapNodesPlugin.log("CHEST END SIZE: " + chestContents.length);

                SchedulerUtil.runSync(() -> chest.setContents(chestContents));
            }
        }
    }

    /** Spawn random items into empty chests. */
    @EventHandler(priority= EventPriority.MONITOR)
    public void onChest(BlockPlaceEvent event) {
        if (event.getBlock().getState() instanceof org.bukkit.block.Chest) {
            chests.add(event.getBlock().getLocation().toVector().toBlockVector());
        }
    }
}
