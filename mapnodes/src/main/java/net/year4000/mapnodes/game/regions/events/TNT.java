/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.game.regions.events;

import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.regions.EventType;
import net.year4000.mapnodes.api.game.regions.RegionListener;
import net.year4000.mapnodes.api.game.regions.EventTypes;
import net.year4000.mapnodes.game.regions.RegionEvent;
import net.year4000.mapnodes.game.regions.types.Point;
import net.year4000.mapnodes.utils.*;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.TimeUnit;

@EventType(EventTypes.TNT)
public class TNT extends RegionEvent implements RegionListener {
    private static final Random rand = new Random();
    private static Map<UUID, Vector> tracker = Maps.newHashMap();

    /** Tnt will be active when placed */
    private boolean instant = false;

    /** The delay the instant tnt will explode */
    @SerializedName("instant_delay")
    private TimeDuration instantDelay = new TimeDuration(TimeUnit.SECONDS, 4, false);

    /** The strength of the tnt higher the number the bigger the explosion */
    private int strength = 4;

    @SerializedName("block_damage")
    private boolean blockDamage = true;

    /** The effected radius for drops */
    private float yield = 85;

    /** Should the tnt block drops be thrown instead of dropped */
    @SerializedName("throw_blocks")
    private boolean throwBlocks = false;

    /** If a tnt is place should be ignite it */
    @EventHandler(ignoreCancelled = true)
    public void onTnt(BlockPlaceEvent event) {
        if (event.getBlock().getType() != Material.TNT) return;

        if (!region.inZone(new Point(event.getBlockPlaced().getLocation().toVector().toBlockVector()))) return;

        if (instant) {
            event.getBlock().setType(Material.AIR);

            // Create the tnt to look like it
            final TNTPrimed tnt = event.getPlayer().getWorld().spawn(
                event.getBlock().getLocation().add(0.5, 0.5, 0.5), // center it
                TNTPrimed.class
            );
            NMSHacks.addTNTSource(tnt, event.getPlayer());
            tnt.setFuseTicks(MathUtil.ticks(instantDelay.toSecs() + 1));
            tnt.setYield(yield);

            // Run the explosion later
            explodeLater(tnt.getSource(), tnt, instantDelay.toSecs());
        }
    }

    /** Set the yield and blocks of the tnt. */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onTnt(EntityExplodeEvent event) {
        if (!region.inZone(new Point(event.getLocation().toVector().toBlockVector()))) return;

        event.setYield(yield);

        if (!blockDamage) {
            event.blockList().clear();
        }

        if (throwBlocks && blockDamage) {
            Vector groundZero = event.getLocation().toVector();
            List<Block> blocks = new ArrayList<>(event.blockList());
            final LivingEntity source = (LivingEntity) (event.getEntity() instanceof LivingEntity ? event.getEntity() : event.getEntity() instanceof TNTPrimed && ((TNTPrimed) event.getEntity()).getSource() instanceof LivingEntity ? ((TNTPrimed) event.getEntity()).getSource() : null);

            for (int i = 0; i < blocks.size(); i++) {
                Collections.reverse(blocks);
                Collections.shuffle(blocks);
            }

            blocks = blocks.subList(0, (int) Math.sqrt(blocks.size()) + blocks.size() / 5);

            // Throw the drops
            blocks.forEach(block -> {
                Location loc = block.getLocation();
                Vector vec = loc.toVector();
                double x = Math.sin(vec.getX()) * rand.nextDouble() * (vec.getX() > groundZero.getX() ? 1 : -1);
                double y = rand.nextDouble();
                double z = Math.sin(vec.getZ()) * rand.nextDouble() * (vec.getZ() > groundZero.getZ() ? 1 : -1);
                Vector velocity = new Vector(x, y, z);

                if (block.getType() == Material.TNT) {
                    block.setType(Material.AIR);
                    TNTPrimed tnt = block.getWorld().spawn(loc, TNTPrimed.class);
                    NMSHacks.addTNTSource(tnt, source);
                    tnt.setFuseTicks(MathUtil.ticks(2));
                    tnt.setYield(yield);
                    explodeLater(tnt.getSource(), tnt, 1);
                    tnt.setVelocity(velocity);
                }
                else if (block.getType().isSolid()) {
                    FallingBlock flying = block.getWorld().spawnFallingBlock(event.getLocation(), block.getType(), block.getData());
                    block.setType(Material.AIR);
                    flying.setDropItem(false);
                    flying.setVelocity(velocity);
                    tracker.put(flying.getUniqueId(), event.getLocation().toVector());
                }
            });

            // Clear drops
            event.blockList().clear();
        }

        runGlobalEventTasks(event.getLocation());
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockLand(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof org.bukkit.entity.FallingBlock && throwBlocks) {
            Vector vector = event.getBlock().getLocation().toVector();
            boolean tooClose = vector.distance(tracker.getOrDefault(event.getEntity().getUniqueId(), vector)) < 2;

            if (event.getBlock().getType() == Material.AIR && tooClose) {
                SchedulerUtil.runSync(() -> event.getBlock().setType(Material.AIR));
            }
        }
    }

    /** Explode the Primed TNT later */
    private void explodeLater(Entity entity, Entity id, int delay) {
        SchedulerUtil.runSync(() -> {
            Location loc = id.getLocation();
            boolean fire = id.getFireTicks() > 0;
            id.remove();
            NMSHacks.createExplosion(entity, Common.center(loc), (byte) strength, fire, blockDamage);
        }, MathUtil.ticks(delay));
    }
}
