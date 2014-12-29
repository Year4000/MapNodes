package net.year4000.mapnodes.game.regions.events;

import com.google.common.annotations.Beta;
import com.google.gson.annotations.SerializedName;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.regions.EventType;
import net.year4000.mapnodes.api.game.regions.RegionListener;
import net.year4000.mapnodes.game.regions.EventTypes;
import net.year4000.mapnodes.game.regions.RegionEvent;
import net.year4000.mapnodes.game.regions.types.Point;
import net.year4000.mapnodes.utils.NMSHacks;
import net.year4000.mapnodes.utils.typewrappers.MaterialList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Map;
import java.util.WeakHashMap;

@EventType(EventTypes.BOW)
public class Bow extends RegionEvent implements RegionListener {
    private EntityType entity = null;
    private Double power = null;
    private MaterialList<Material> blocks = new MaterialList<>(Arrays.asList(Material.values()));
    private boolean explode = false;
    @Beta
    @SerializedName("break_block")
    private boolean breakBlock = false;
    private boolean impactTasks = false;

    private transient Map<Integer, Entity> entityIds = new WeakHashMap<>();

    @EventHandler(ignoreCancelled = true)
    public void onBowShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        if (!region.inZone(new Point(event.getEntity().getLocation().toVector().toBlockVector()))) return;

        GamePlayer player = region.getGame().getPlayer((Player) event.getEntity());

        if (applyToPlayer(player)) {
            LivingEntity livingEntity = player.getPlayer();
            Vector vector = event.getProjectile().getVelocity();
            Vector adjustment = vector.normalize().multiply(3);
            Location loc = livingEntity.getEyeLocation().clone().add(adjustment);

            // Set bow's entity if set
            if (entity != null) {
                Entity bowEntity = MapNodes.getCurrentWorld().spawnEntity(loc, entity);
                event.setProjectile(bowEntity);
                NMSHacks.addShooter(bowEntity, event.getEntity());
                bowEntity.setVelocity(vector);
            }

            // Entity not set but still set velocity of projectile
            if (power != null) {
                event.getProjectile().getVelocity().multiply(power);
            }

            // Calculate the arrow damage of the bow
            if (event.getBow().containsEnchantment(Enchantment.ARROW_DAMAGE)) {
                event.getProjectile().getVelocity().multiply(1 + event.getBow().getEnchantmentLevel(Enchantment.ARROW_DAMAGE));
            }

            entityIds.put(event.getProjectile().getEntityId(), livingEntity);

            if (!impactTasks) {
                runGlobalEventTasks(player);
                runGlobalEventTasks(player.getPlayer().getLocation());
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(ProjectileHitEvent event) {
        if (entityIds.containsKey(event.getEntity().getEntityId())) {
            Location loc = event.getEntity().getLocation();
            Entity shooter = entityIds.get(event.getEntity().getEntityId());

            // Teleport player if entity is an ender pearl
            if (event.getEntity() instanceof EnderPearl) {
                loc.setPitch(shooter.getLocation().getPitch());
                loc.setYaw(shooter.getLocation().getYaw());
                shooter.teleport(loc, PlayerTeleportEvent.TeleportCause.ENDER_PEARL);
            }

            // Create an explosion if true
            if (explode) {
                boolean fire = event.getEntity().getFireTicks() > 0;
                event.getEntity().remove();
                NMSHacks.createExplosion(shooter, loc, (byte) 2, fire, true);
            }

            // Break the impact block
            if (breakBlock) {
                BlockIterator itr = new BlockIterator(MapNodes.getCurrentWorld(), loc.toVector(), event.getEntity().getVelocity().normalize(), 0, 2);
                Block block = null;

                while (itr.hasNext()) {
                    block = itr.next();

                    if (block.getType() != Material.AIR) {
                        break;
                    }
                }

                if (block != null && blocks.contains(block.getType())) {
                    block.setType(Material.AIR);
                    block.getWorld().playSound(block.getLocation(), Sound.ITEM_PICKUP, 5F, 5F);
                }
            }

            if (impactTasks) {
                runGlobalEventTasks(region.getGame().getPlayer((Player) event.getEntity()));
                runGlobalEventTasks(loc);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        if (entityIds.containsKey(event.getDamager().getEntityId())) {
            if (event.getDamager() instanceof Projectile) {
                Vector vector = event.getDamager().getVelocity();

                if (power != null) {
                    vector.multiply(power);
                }

                double damage = Math.abs(vector.getX()) + Math.abs(vector.getY()) + Math.abs(vector.getZ());
                event.setDamage(Math.abs(damage + Math.sqrt(damage)));
            }
        }
    }
}
