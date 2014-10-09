package net.year4000.mapnodes.addons.modules.misc;

import net.year4000.mapnodes.addons.Addon;
import net.year4000.mapnodes.addons.AddonInfo;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.game.NodeKit;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.List;

@AddonInfo(
    name = "Game Mech",
    version = "1.0",
    description = "Change vanilla mechanics to better serve the game.",
    listeners = {GameMech.class}
)
public class GameMech extends Addon implements Listener {
    /** Instant kill the player when they fell in the void */
    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageEvent event) {
        // If not a player don't check
        if (!(event.getEntity() instanceof Player)) return;

        // If playing instant kill when damaged by void
        if (MapNodes.getCurrentGame().getPlayer((Player) event.getEntity()).isPlaying()) {
            // If the damage is void reset player
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                EntityDamageEvent death = new EntityDamageEvent(event.getEntity(), EntityDamageEvent.DamageCause.VOID, 0);
                event.getEntity().setLastDamageCause(death);
                ((Player) event.getEntity()).setHealth(0);
                event.setCancelled(true);
            }
        }
    }

    /** Always remove the color from the leather armor */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        List<ItemStack> filterList = new ArrayList<>(event.getDrops());

        filterList.forEach(item -> {
            if (item.getItemMeta() instanceof LeatherArmorMeta) {
                LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
                if (!meta.getColor().equals(Color.fromRGB(Integer.valueOf(NodeKit.DEFAULT_LEATHER, 16)))) {
                    event.getDrops().remove(item);
                    meta.setColor(Color.fromRGB(Integer.valueOf(NodeKit.DEFAULT_LEATHER, 16)));
                    item.setItemMeta(meta);
                    event.getDrops().add(item);
                }
            }
        });
    }
}
