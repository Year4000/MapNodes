package net.year4000.mapnodes.game.regions.events;

import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.events.game.GameClockEvent;
import net.year4000.mapnodes.api.game.regions.EventType;
import net.year4000.mapnodes.api.game.regions.RegionListener;
import net.year4000.mapnodes.game.regions.EventTypes;
import net.year4000.mapnodes.game.regions.RegionEvent;
import net.year4000.mapnodes.utils.typewrappers.MaterialList;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.util.Arrays;

@EventType(EventTypes.ITEM_DROP)
public class ItemDrop extends RegionEvent implements RegionListener {
    private MaterialList<Material> items = new MaterialList<>(Arrays.asList(Material.values()));

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPickup(PlayerPickupItemEvent event) {
        Material material = event.getItem().getItemStack().getType();

        if (isAllowSet()) {
            if ((isAllow() && items.contains(material)) || (!isAllow() && !items.contains(material))) {
                event.setCancelled(false);
            }
            else if ((isAllow() && !items.contains(material)) || (!isAllow() && items.contains(material))) {
                event.setCancelled(true);
                event.getItem().remove();
            }
        }
    }

    @EventHandler
    public void onStart(GameClockEvent event) {
        // Cycle through the world and remove items that should not be their
        MapNodes.getCurrentWorld().getEntitiesByClass(Item.class).stream()
            .forEach(item -> {
                Material material = item.getItemStack().getType();
                if ((isAllow() && !items.contains(material)) || (!isAllow() && items.contains(material))) {
                    item.remove();
                }
            });
    }
}
