package net.year4000.mapnodes.addons.modules.misc;

import com.google.common.collect.ImmutableSet;
import net.year4000.mapnodes.addons.Addon;
import net.year4000.mapnodes.addons.AddonInfo;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.game.NodeKit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    // Vars for item pick up repairs
    private static final Set<Material> mergeItems = ImmutableSet.<Material>builder()
        // Diamond 1562
        .add(Material.DIAMOND_SWORD)
        .add(Material.DIAMOND_AXE)
        .add(Material.DIAMOND_HOE)
        .add(Material.DIAMOND_PICKAXE)
        .add(Material.DIAMOND_SPADE)
        // Iron 251
        .add(Material.IRON_SWORD)
        .add(Material.IRON_AXE)
        .add(Material.IRON_HOE)
        .add(Material.IRON_PICKAXE)
        .add(Material.IRON_SPADE)
         // Stone 132
        .add(Material.STONE_SWORD)
        .add(Material.STONE_AXE)
        .add(Material.STONE_HOE)
        .add(Material.STONE_PICKAXE)
        .add(Material.STONE_SPADE)
        // Wood 60
        .add(Material.WOOD_SWORD)
        .add(Material.WOOD_AXE)
        .add(Material.WOOD_HOE)
        .add(Material.WOOD_PICKAXE)
        .add(Material.WOOD_SPADE)
        // Gold 33
        .add(Material.GOLD_SWORD)
        .add(Material.GOLD_AXE)
        .add(Material.GOLD_HOE)
        .add(Material.GOLD_PICKAXE)
        .add(Material.GOLD_SPADE)

        .add(Material.CARROT_STICK) // 26
        .add(Material.FLINT_AND_STEEL) // 65
        .add(Material.FISHING_ROD) // 65
        .add(Material.SHEARS) // 239
        .add(Material.BOW) // 385

        .add(Material.DIAMOND_HELMET) // 364
        .add(Material.DIAMOND_CHESTPLATE) // 529
        .add(Material.DIAMOND_LEGGINGS) // 496
        .add(Material.DIAMOND_BOOTS) // 430

        .add(Material.IRON_HELMET) // 166
        .add(Material.IRON_CHESTPLATE) // 241
        .add(Material.IRON_LEGGINGS) // 226
        .add(Material.IRON_BOOTS) // 196

        .add(Material.CHAINMAIL_HELMET) // 166
        .add(Material.CHAINMAIL_CHESTPLATE) // 241
        .add(Material.CHAINMAIL_LEGGINGS) // 226
        .add(Material.CHAINMAIL_BOOTS) // 196

        .add(Material.GOLD_HELMET) // 78
        .add(Material.GOLD_CHESTPLATE) // 113
        .add(Material.GOLD_LEGGINGS) // 106
        .add(Material.GOLD_BOOTS) // 92

        .add(Material.LEATHER_HELMET) // 56
        .add(Material.LEATHER_CHESTPLATE) // 81
        .add(Material.LEATHER_LEGGINGS) // 76
        .add(Material.LEATHER_BOOTS) // 66
        .build();

    /** Merge items when they are picked up, this will repair if needed */
    @EventHandler(ignoreCancelled = true)
    public void onPickup(PlayerPickupItemEvent event) {
        ItemStack item = event.getItem().getItemStack();
        PlayerInventory inv = event.getPlayer().getInventory();

        if (!mergeItems.contains(item.getType())) return;

        if (inv.getHelmet() != null && inv.getHelmet().getType() == item.getType()) {
            inv.getHelmet().setDurability(dura(inv.getHelmet(), item));
            event.getItem().remove();
            event.setCancelled(true);
            return;
        }

        if (inv.getChestplate() != null && inv.getChestplate().getType() == item.getType()) {
            inv.getChestplate().setDurability(dura(inv.getChestplate(), item));
            event.getItem().remove();
            event.setCancelled(true);
            return;
        }

        if (inv.getLeggings() != null && inv.getLeggings().getType() == item.getType()) {
            inv.getLeggings().setDurability(dura(inv.getLeggings(), item));
            event.getItem().remove();
            event.setCancelled(true);
            return;
        }

        if (inv.getBoots() != null && inv.getBoots().getType() == item.getType()) {
            inv.getBoots().setDurability(dura(inv.getBoots(), item));
            event.getItem().remove();
            event.setCancelled(true);
            return;
        }

        for (int i = 0; i < inv.getSize() ; i++) {
            ItemStack itemSlot = inv.getItem(i);

            if (itemSlot == null || itemSlot.getType() != item.getType()) continue;
            // System.out.println(i + " : " + itemSlot.toString());

            itemSlot.setDurability(dura(itemSlot, item));
            event.getItem().remove();
            event.setCancelled(true);
            inv.setItem(i, itemSlot);
            break;
        }
    }

    public short dura(ItemStack old, ItemStack pickup) {
        return (short) (old.getDurability() - pickup.getDurability());
    }
}
