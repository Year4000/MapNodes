package net.year4000.mapnodes.listeners;

import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.game.NodeGame;
import net.year4000.mapnodes.game.components.regions.types.Point;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class RegionListener implements Listener {
    @EventHandler
    public void onMove(PlayerMoveEvent event) {

    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Vector v = event.getBlockPlaced().getLocation().toVector();
        Point point = new Point(v.getBlockX(), v.getBlockY(), v.getBlockZ());

        ((NodeGame) MapNodes.getCurrentGame()).getRegions().values().stream()
            .filter(r -> r.inZone(point))
            .forEach(r -> MapNodesPlugin.log(point + " is in a region!"));
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {

    }

    @EventHandler
    public void onMelt(BlockFadeEvent event) {

    }

    @EventHandler
    public void onFlow(BlockFromToEvent event) {

    }
}
