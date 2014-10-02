package net.year4000.mapnodes.game.regions.events;

import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.game.regions.EventType;
import net.year4000.mapnodes.game.regions.EventTypes;
import net.year4000.mapnodes.game.regions.RegionEvent;
import net.year4000.mapnodes.game.regions.RegionListener;
import net.year4000.mapnodes.game.regions.types.Point;
import net.year4000.mapnodes.utils.typewrappers.MaterialList;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Arrays;

@EventType(EventTypes.DESTROY)
public class Destroy extends RegionEvent implements RegionListener {
    private MaterialList<Material> blocks = new MaterialList<>(Arrays.asList(Material.values()));

    @EventHandler
    public void onPlace(BlockBreakEvent event) {
        if (!region.inZone(new Point(event.getBlock().getLocation().toVector().toBlockVector()))) return;

        GamePlayer player = region.getGame().getPlayer(event.getPlayer());
        Material material = event.getBlock().getType();

        if (applyToPlayer(player)) {
            if (isAllow() && blocks.contains(material) || !isAllow() && !blocks.contains(material)) {
                event.setCancelled(false);
            }
            else if (!isAllow() && blocks.contains(material)) {
                event.setCancelled(true);
            }

            runGlobalEventTasks(player);
        }
    }
}
