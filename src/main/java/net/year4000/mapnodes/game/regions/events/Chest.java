package net.year4000.mapnodes.game.regions.events;

import com.google.common.collect.Iterables;
import com.google.gson.annotations.SerializedName;
import net.year4000.mapnodes.api.events.game.GameStopEvent;
import net.year4000.mapnodes.game.NodeKit;
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
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BlockVector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

@EventType(EventTypes.CHEST)
public class Chest extends RegionEvent implements RegionListener {
    private static transient final Random rand = new Random(System.currentTimeMillis());
    private static transient final List<BlockVector> chests = new CopyOnWriteArrayList<>();
    private static transient final List<BlockVector> placedChests = new CopyOnWriteArrayList<>();
    private List<String> kits = new ArrayList<>();
    private ItemStackList<ItemStack> items = new ItemStackList<>();
    @SerializedName("keep_filled")
    private boolean keepFilled = false;
    private boolean fill = false;
    private int amount = 0;
    private boolean scatter = false;

    /** Spawn random items into empty chests. */
    @EventHandler
    public void onChest(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

            final BlockVector location = event.getClickedBlock().getLocation().toVector().toBlockVector();

            if (!shouldRunEvent(new Point(location)) || chests.contains(location) || placedChests.contains(location)) return;


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
            if (ChestUtil.isEmpty(chest)) {
                chests.add(location);

                // Remove chest from location when no viewers are looking
                if (keepFilled) {
                    class ChestLoop implements Runnable {
                        private BukkitTask task;

                        public ChestLoop() {
                            task = SchedulerUtil.repeatAsync(this, 20L * 5L);
                        }

                        @Override
                        public void run() {
                            boolean remove = chest instanceof DoubleChestInventory ? ((DoubleChestInventory) chest).getLeftSide().getViewers().size() + ((DoubleChestInventory) chest).getRightSide().getViewers().size() == 0 : chest.getViewers().size() == 0;

                            if (remove) {
                                chests.remove(location);
                                chest.setContents(new ItemStack[chest.getSize()]);
                                task.cancel();
                            }
                        }
                    }

                    new ChestLoop();
                }

                // Add items from the kit to the chests
                if (kits.size() > 0) {
                    kits.forEach(kit -> {
                        NodeKit nodeKit = region.getGame().getKits().get(kit);
                        items.addAll(nodeKit.getItems().getNonAirItems());
                    });
                }

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

                SchedulerUtil.runSync(() -> chest.setContents(chestContents), 2L);
            }
        }
    }

    /** Spawn random items into empty chests. */
    @EventHandler(priority= EventPriority.MONITOR)
    public void onChest(BlockPlaceEvent event) {
        if (event.getBlock().getState() instanceof org.bukkit.block.Chest) {
            placedChests.add(event.getBlock().getLocation().toVector().toBlockVector());
        }
    }

    /** Clear out the chests lists when the game stops */
    @EventHandler
    public void onGameEnd(GameStopEvent event) {
        chests.clear();
        placedChests.clear();
    }
}
