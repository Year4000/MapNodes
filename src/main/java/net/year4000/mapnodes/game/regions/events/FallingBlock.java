/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.game.regions.events;

import com.google.gson.annotations.SerializedName;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.regions.EventType;
import net.year4000.mapnodes.api.game.regions.RegionListener;
import net.year4000.mapnodes.game.regions.EventTypes;
import net.year4000.mapnodes.game.regions.RegionEvent;
import net.year4000.mapnodes.game.regions.types.Point;
import net.year4000.mapnodes.utils.SchedulerUtil;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockFallEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.util.Vector;

import java.util.Random;

@EventType(EventTypes.FALLING_BLOCKS)
public class FallingBlock extends RegionEvent implements RegionListener {
    private static final Random rand = new Random();

    @SerializedName("break_block")
    private boolean breakBlock = false;

    @EventHandler(ignoreCancelled = true)
    public void onStateChange(BlockFallEvent event) {
        if (!region.inZone(new Point(event.getBlock().getLocation().toVector().toBlockVector()))) return;

        if (isAllowSet() && !isAllow()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockLand(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof org.bukkit.entity.FallingBlock) {
            if (event.getBlock().getType() == Material.AIR && breakBlock) {
                SchedulerUtil.runSync(() -> {
                    event.getBlock().setType(Material.AIR);
                    Location loc = event.getBlock().getLocation();
                    MapNodes.getCurrentGame().getPlaying()
                        .filter(player -> player.getPlayer().getLocation().distance(loc) < 100)
                        .map(GamePlayer::getPlayer)
                        .forEach(player -> {
                            for (int i = 0; i < 10; i++) {
                                player.playEffect(splater(loc), Effect.VOID_FOG, 1);
                            }
                        });
                });
            }
        }
    }

    private Location splater(Location loc) {
        return loc.clone().add(new Vector(rand.nextDouble(), rand.nextDouble() + 0.5, rand.nextDouble()));
    }
}
