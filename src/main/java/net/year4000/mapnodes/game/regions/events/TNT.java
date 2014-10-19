package net.year4000.mapnodes.game.regions.events;

import com.google.gson.annotations.SerializedName;
import net.year4000.mapnodes.game.regions.EventType;
import net.year4000.mapnodes.game.regions.EventTypes;
import net.year4000.mapnodes.game.regions.RegionEvent;
import net.year4000.mapnodes.game.regions.RegionListener;
import net.year4000.mapnodes.game.regions.types.Point;
import net.year4000.mapnodes.utils.MathUtil;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.mapnodes.utils.TimeDuration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.TimeUnit;

@EventType(EventTypes.TNT)
public class TNT extends RegionEvent implements RegionListener {
    private static final Random rand = new Random();
    private static Collection<? extends Entity> tracker = new ArrayList<>();

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
            tnt.setFuseTicks(MathUtil.ticks(instantDelay.toSecs() + 1));
            tnt.setYield(yield);

            // Run the explosion later
            explodeLater(tnt, instantDelay.toSecs());
        }
    }

    /** Set the yield and blocks of the tnt. */
    @EventHandler(ignoreCancelled = true, priority=EventPriority.HIGH)
    public void onTnt(EntityExplodeEvent event) {
        if (!region.inZone(new Point(event.getLocation().toVector().toBlockVector()))) return;

        event.setYield(yield);

        if (!blockDamage) {
            event.blockList().clear();
        }

        if (throwBlocks && blockDamage) {
            Vector groundZero = event.getLocation().toVector();
            List<Block> blocks = new ArrayList<>(event.blockList());

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
                    tnt.setFuseTicks(MathUtil.ticks(2));
                    tnt.setYield(yield);
                    explodeLater(tnt, 1);
                    tnt.setVelocity(velocity);
                }
                else if (block.getType().isSolid()) {
                    FallingBlock flying = block.getWorld().spawnFallingBlock(event.getLocation(), block.getType(), block.getData());
                    block.setType(Material.AIR);
                    flying.setDropItem(false);
                    flying.setVelocity(velocity);
                }
            });

            // Clear drops
            event.blockList().clear();
        }

        runGlobalEventTasks(event.getLocation());
    }

    /** Explode the Primed TNT later */
    private void explodeLater(Entity id, int delay) {
        Location loc = id.getLocation();

        SchedulerUtil.runSync(() -> {
            id.remove();
            loc.getWorld().createExplosion(
                loc.getX() + 0.5, // center it
                loc.getY() + 0.5, // center it
                loc.getZ() + 0.5, // center it
                strength, // the strength of tnt
                false,
                blockDamage
            );
        }, MathUtil.ticks(delay));
    }
}
