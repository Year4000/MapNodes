package net.year4000.mapnodes.game.regions.events;

import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.events.game.GameClockEvent;
import net.year4000.mapnodes.api.events.game.GameStopEvent;
import net.year4000.mapnodes.game.regions.EventType;
import net.year4000.mapnodes.game.regions.EventTypes;
import net.year4000.mapnodes.game.regions.RegionEvent;
import net.year4000.mapnodes.game.regions.RegionListener;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.mapnodes.utils.typewrappers.MaterialList;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;

@EventType(EventTypes.ITEM_DROP)
public class ItemDrop extends RegionEvent implements RegionListener {
    private MaterialList<Material> items = new MaterialList<>(Arrays.asList(Material.values()));

    private transient BukkitTask task;

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        Material material = event.getItem().getItemStack().getType();
        if ((isAllow() && items.contains(material)) || (!isAllow() && !items.contains(material))) {
            event.setCancelled(false);
        }
        else if ((isAllow() && !items.contains(material)) || (!isAllow() && items.contains(material))) {
            event.setCancelled(true);
            event.getItem().remove();
        }
    }

    @EventHandler
    public void onStart(GameClockEvent event) {
        if (task == null) {
            task = SchedulerUtil.repeatSync(() -> {
                // Cycle through the world and remove items that should not be their
                MapNodes.getCurrentWorld().getEntitiesByClass(Item.class).stream()
                    .forEach(item -> {
                        Material material = item.getItemStack().getType();
                        /*if ((isAllow() && items.contains(material)) || (!isAllow() && !items.contains(material))) {
                            // Item is allowed may or may not need this method
                        }
                        else */if ((isAllow() && !items.contains(material)) || (!isAllow() && items.contains(material))) {
                            item.remove();
                        }
                    });
            }, 1L);
        }
    }

    @EventHandler
    public void onStop(GameStopEvent event) {
        if (task != null) {
            task.cancel();
        }
        else {
            MapNodesPlugin.debug("The item remover clock was null, this should not happen.");
        }
    }
}
