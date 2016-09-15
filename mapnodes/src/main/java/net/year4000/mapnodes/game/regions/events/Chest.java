/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.game.regions.events;

import com.google.common.collect.Iterables;
import com.google.gson.annotations.SerializedName;
import net.year4000.mapnodes.api.events.game.GameStopEvent;
import net.year4000.mapnodes.api.game.GameKit;
import net.year4000.mapnodes.api.game.regions.EventType;
import net.year4000.mapnodes.api.game.regions.EventTypes;
import net.year4000.mapnodes.api.game.regions.RegionListener;
import net.year4000.mapnodes.game.regions.RegionEvent;
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

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@EventType(EventTypes.CHEST)
public class Chest extends RegionEvent implements RegionListener {
    private static transient final Random rand = new Random(System.currentTimeMillis());
    private static transient final List<BlockVector> chests = new CopyOnWriteArrayList<>();
    private static transient final List<BlockVector> placedChests = new CopyOnWriteArrayList<>();
    private List<ChestKit> kits = new ArrayList<>();
    private String kit = null;
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

            if (!shouldRunEvent(new Point(location)) || chests.contains(location) || placedChests.contains(location)) {
                return;
            }


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
                final ItemStack[] chestContents = new ItemStack[chest.getSize()];

                // Place items in chest based on kit list
                if (kits.size() > 0 && kit == null) {
                    List<ItemStack> chestItems = new ArrayList<>();

                    kits.forEach(kit -> {
                        GameKit nodeKit = region.getGame().getKits().get(kit.getName());

                        if (kit.isRepeat()) {
                            for (int i = 0; i < (kit.getAmount() > chest.getSize() ? chest.getSize() : kit.getAmount()); i++) {
                                chestItems.add(nodeKit.getNonAirItems().get(Math.abs(rand.nextInt(nodeKit.getNonAirItems().size()))));
                            }
                        }
                        else {
                            List<ItemStack> itemStacks = nodeKit.getNonAirItems();
                            Collections.shuffle(itemStacks);

                            for (int i = 0; i < (kit.getAmount() > chest.getSize() ? chest.getSize() : kit.getAmount()); i++) {
                                chestItems.add(itemStacks.get(i));
                            }
                        }

                    });

                    for (int i = 0; i < (chestItems.size() > chest.getSize() ? chest.getSize() : chestItems.size()); i++) {
                        chestContents[i] = chestItems.get(i);
                    }
                }
                else {
                    // If one kit treat it like external list
                    if (kit != null) {
                        GameKit nodeKit = region.getGame().getKits().get(kit);
                        items.addAll(nodeKit.getNonAirItems());
                    }

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
                }

                // MapNodesPlugin.log("CHEST END SIZE: " + chestContents.length);

                SchedulerUtil.runSync(() -> chest.setContents(chestContents), 2L);
            }
        }
    }

    /** Spawn random items into empty chests. */
    @EventHandler(priority = EventPriority.MONITOR)
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

    public static class ChestKit {
        private String name;
        private int amount;
        private boolean repeat = false;

        public ChestKit() {
        }

        public String getName() {
            return this.name;
        }

        public int getAmount() {
            return this.amount;
        }

        public boolean isRepeat() {
            return this.repeat;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public void setRepeat(boolean repeat) {
            this.repeat = repeat;
        }

        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof ChestKit)) return false;
            final ChestKit other = (ChestKit) o;
            if (!other.canEqual((Object) this)) return false;
            final Object this$name = this.getName();
            final Object other$name = other.getName();
            if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
            if (this.getAmount() != other.getAmount()) return false;
            if (this.isRepeat() != other.isRepeat()) return false;
            return true;
        }

        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $name = this.getName();
            result = result * PRIME + ($name == null ? 43 : $name.hashCode());
            result = result * PRIME + this.getAmount();
            result = result * PRIME + (this.isRepeat() ? 79 : 97);
            return result;
        }

        protected boolean canEqual(Object other) {
            return other instanceof ChestKit;
        }

        public String toString() {
            return "net.year4000.mapnodes.game.regions.events.Chest.ChestKit(name=" + this.getName() + ", amount=" + this.getAmount() + ", repeat=" + this.isRepeat() + ")";
        }
    }
}
