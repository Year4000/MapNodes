package net.year4000.mapnodes.addons.modules.spectator;

import net.year4000.mapnodes.addons.Addon;
import net.year4000.mapnodes.addons.AddonInfo;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

@AddonInfo(
    name = "Open Inventories",
    version = "1.4",
    description = "View other inventories as a spectator.",
    listeners = {OpenInventories.class}
)
public class OpenInventories extends Addon implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void openInv(PlayerInteractEntityEvent event) {
        GameManager gm = MapNodes.getCurrentGame();
        GamePlayer gPlayer = gm.getPlayer(event.getPlayer());

        if (gPlayer.isSpectator()) {
            if (event.getRightClicked() instanceof Player) {
                Player rightClicked = (Player) event.getRightClicked();

                if (!gm.getPlayer(rightClicked).isSpectator()) {
                    gPlayer.getPlayer().openInventory(openPlayer(gm.getPlayer(rightClicked)));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void openInv(PlayerInteractEvent event) {
        GameManager gm = MapNodes.getCurrentGame();
        GamePlayer gPlayer = gm.getPlayer(event.getPlayer());

        if (gPlayer.isSpectator() && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();

            if (block.getState() instanceof InventoryHolder) {
                SchedulerUtil.runSync(() -> {
                    Inventory inv = ((InventoryHolder) block.getState()).getInventory();

                    // Create a fake inventory so the chest don't really open
                    Inventory fake = Bukkit.createInventory(null, inv.getSize(), inv.getTitle());
                    fake.setContents(inv.getContents());

                    gPlayer.getPlayer().openInventory(fake);
                });
            }
        }
    }

    /** Create an inventory of the player stats. */
    private Inventory openPlayer(GamePlayer gPlayer) {
        final int SIZE = 45;
        ItemStack[] items = new ItemStack[SIZE];
        Player player = gPlayer.getPlayer();
        PlayerInventory pinv = player.getInventory();

        // Armor
        items[0] = pinv.getHelmet();
        items[1] = pinv.getChestplate();
        items[2] = pinv.getLeggings();
        items[3] = pinv.getBoots();

        // Health and Food
        items[8] = getHunger(player);
        items[7] = getHealth(player);

        // Items
        for (int i = 0; i < 36; i++) {
            // Hot Bar
            if (i < 9) {
                boolean empty = pinv.getItem(i) == null;
                items[(45-9) + i] = empty ? new ItemStack(Material.AIR) : pinv.getItem(i);
            }
            // Backpack
            else {
                boolean empty = pinv.getItem(i) == null;
                items[i] = empty ? new ItemStack(Material.AIR) : pinv.getItem(i);
            }
        }

        Inventory inv = Bukkit.createInventory(null, SIZE, gPlayer.getPlayerColor());
        inv.setContents(items);

        return inv;
    }

    /** Get the heal for the player. */
    private ItemStack getHealth(Player player) {
        int health = (int) player.getHealth();

        ItemStack level =  new ItemStack(Material.SPECKLED_MELON, health);
        ItemMeta meta = level.getItemMeta();
        meta.setDisplayName(MessageUtil.message(Msg.locale(player, "inv.health")));
        level.setItemMeta(meta);

        return level;
    }

    /** Get the hunger for the player. */
    private ItemStack getHunger(Player player) {
        int hunger = player.getFoodLevel();

        ItemStack level =  new ItemStack(Material.COOKED_BEEF, hunger);
        ItemMeta meta = level.getItemMeta();
        meta.setDisplayName(MessageUtil.message(Msg.locale(player, "inv.hunger")));
        level.setItemMeta(meta);

        return level;
    }
}
