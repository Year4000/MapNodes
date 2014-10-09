package net.year4000.mapnodes.listeners;

import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.events.player.GamePlayerJoinSpectatorEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerJoinTeamEvent;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.game.NodeGame;
import net.year4000.mapnodes.game.NodeKit;
import net.year4000.mapnodes.game.NodePlayer;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.utilities.bukkit.FunEffectsUtil;
import net.year4000.utilities.bukkit.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

@EqualsAndHashCode
public class SpectatorListener implements Listener {
    /** Stop the event if the player is not playing the game */
    private void stopEvent(Cancellable event, Player player) {
        if (!MapNodes.getCurrentGame().getPlayer(player).isPlaying()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onDrop(PlayerDropItemEvent event) {
        stopEvent(event, event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onInteract(PlayerInteractEvent event) {
        stopEvent(event, event.getPlayer());
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntityTarget(EntityTargetLivingEntityEvent event) {
        if (!(event.getTarget() instanceof Player)) return;

        stopEvent(event, (Player) event.getTarget());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onInteract(EntityInteractEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        stopEvent(event, (Player) event.getEntity());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onIvnInteract(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        stopEvent(event, (Player) event.getWhoClicked());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;

        stopEvent(event, (Player) event.getDamager());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPickUp(PlayerPickupItemEvent event) {
        stopEvent(event, event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPickUp(PlayerPickupExperienceEvent event) {
        stopEvent(event, event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onDamage(EntityDamageEvent event) {
        // If not a player don't check
        if (!(event.getEntity() instanceof Player)) return;
        Player entity = (Player) event.getEntity();

        // If not playing send player back to spawn
        if (!MapNodes.getCurrentGame().getPlayer(entity).isPlaying()) {
            // If the damage is void reset player
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                // Teleport back to main spawn
                entity.teleport(((NodeGame) MapNodes.getCurrentGame()).getConfig().getSafeRandomSpawn());
                // Re enable flying
                entity.setAllowFlight(true);
                entity.setFlying(true);
            }

            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onMove(PlayerMoveEvent event) {
        // Help the spectator ignore side effects
        if (!MapNodes.getCurrentGame().getPlayer(event.getPlayer()).isPlaying()) {
            event.getPlayer().setFireTicks(0);
            event.getPlayer().setFoodLevel(20);
        }
    }

    // Open Player's Inventory //

    @EventHandler(priority = EventPriority.MONITOR)
    public void updateInventory(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            GamePlayer player = MapNodes.getCurrentGame().getPlayer((Player) event.getEntity());
            ((NodePlayer) player).updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void updateInventory(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            GamePlayer player = MapNodes.getCurrentGame().getPlayer((Player) event.getWhoClicked());
            ((NodePlayer) player).updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void updateInventory(PlayerPickupItemEvent event) {
        GamePlayer player = MapNodes.getCurrentGame().getPlayer(event.getPlayer());
        ((NodePlayer) player).updateInventory();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void updateInventory(PlayerDropItemEvent event) {
        GamePlayer player = MapNodes.getCurrentGame().getPlayer(event.getPlayer());
        ((NodePlayer) player).updateInventory();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void updateInventory(PlayerItemConsumeEvent event) {
        GamePlayer player = MapNodes.getCurrentGame().getPlayer(event.getPlayer());
        ((NodePlayer) player).updateInventory();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void updateInventory(PlayerItemBreakEvent event) {
        GamePlayer player = MapNodes.getCurrentGame().getPlayer(event.getPlayer());
        ((NodePlayer) player).updateInventory();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void openInv(PlayerInteractEntityEvent event) {
        GameManager gm = MapNodes.getCurrentGame();
        GamePlayer gPlayer = gm.getPlayer(event.getPlayer());

        if (!gPlayer.isPlaying()) {
            if (event.getRightClicked() instanceof Player) {
                GamePlayer rightClicked = gm.getPlayer((Player) event.getRightClicked());

                if (rightClicked.isPlaying()) {
                    gPlayer.getPlayer().openInventory(rightClicked.getInventory());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void openInv(PlayerInteractEvent event) {
        GameManager gm = MapNodes.getCurrentGame();
        GamePlayer gPlayer = gm.getPlayer(event.getPlayer());

        if (!gPlayer.isPlaying() && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();

            if (block.getState() instanceof InventoryHolder) {
                Inventory inv = ((InventoryHolder) block.getState()).getInventory();

                if (block.getState() instanceof Chest) {
                    SchedulerUtil.runSync(() -> {

                        // Create a fake inventory so the chest don't really open
                        Inventory fake = Bukkit.createInventory(null, inv.getSize(), inv.getTitle());
                        fake.setContents(inv.getContents());

                        gPlayer.getPlayer().openInventory(fake);
                    }, 5L);
                }
                else {
                    gPlayer.getPlayer().openInventory(inv);
                }
            }
        }
    }

    // Map Book //

    public ItemStack book(GamePlayer player) {
        GameManager gm = MapNodes.getCurrentGame();

        return ItemUtil.createBook(
            "&b" + gm.getMap().getName(),
            "&5&o" + gm.getMap().getMainAuthor(),
            ((NodeGame) gm).getBookPages(player.getPlayer())
        );
    }

    @EventHandler
    public void onTeamJoin(GamePlayerJoinTeamEvent e) {
        GameManager gm = MapNodes.getCurrentGame();

        SchedulerUtil.runSync(() -> gm.getSpectating().parallel().forEach(player -> {
            Inventory inv = player.getPlayer().getInventory();

            inv.setItem(2, book(player));
            player.getPlayer().updateInventory();
        }), 5L);
    }

    @EventHandler
    public void onJoin(GamePlayerJoinSpectatorEvent e) {
        NodeKit kit = (NodeKit) e.getKit();

        // Book
        kit.getItems().set(2, book(e.getPlayer()));
        // Servers
        kit.getItems().set(8, ItemUtil.makeItem("nether_star", "{'display':{'name':'" + Msg.locale(e.getPlayer(), "items.servers_menu") + "'}}"));
        // Game Menu
        kit.getItems().set(0, ItemUtil.makeItem("eye_of_ender", "{'display':{'name':'" + Msg.locale(e.getPlayer(), "team.menu.item") + "'}}"));
    }

    // Team picker GUI //

    @EventHandler
    public void onTeamPicker(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        GamePlayer gPlayer = MapNodes.getCurrentGame().getPlayer(player);

        if (gPlayer.isPlaying()) return;

        if (Msg.matches(gPlayer, event.getInventory().getName(), "team.menu.title")) {
            try {
                ItemStack item = event.getCurrentItem();
                String teamName = item.getItemMeta().getDisplayName();
                ((NodePlayer) gPlayer).joinTeam(((NodeGame) MapNodes.getCurrentGame()).getTeam(new Locale(player.getLocale()), teamName));
                FunEffectsUtil.playSound(player, Sound.ITEM_PICKUP);
            } catch (NullPointerException e) {
                /** Not a valid item */
                // MapNodesPlugin.debug(e, true);
            } finally {
                player.closeInventory();
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onGameJoiner(PlayerInteractEvent event) {
        boolean rightAir = event.getAction() == Action.RIGHT_CLICK_AIR;
        boolean rightBlock = event.getAction() == Action.RIGHT_CLICK_BLOCK;

        if (rightAir || rightBlock) {
            ItemStack hand = event.getPlayer().getItemInHand();
            GamePlayer player = MapNodes.getCurrentGame().getPlayer(event.getPlayer());

            if (player.isPlaying()) return;

            try {
                if (Msg.matches(player, hand.getItemMeta().getDisplayName(), "team.menu.item")) {
                    ((NodeGame) MapNodes.getCurrentGame()).openTeamChooserMenu(player);
                }
            } catch (NullPointerException e) {
                /** Not a valid item */
                // MapNodesPlugin.debug(e, true);
            } finally {
                event.setCancelled(true);
            }
        }
    }
}
