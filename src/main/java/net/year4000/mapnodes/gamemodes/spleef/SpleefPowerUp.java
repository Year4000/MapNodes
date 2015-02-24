package net.year4000.mapnodes.gamemodes.spleef;

import com.google.common.collect.ImmutableList;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Common;
import net.year4000.mapnodes.utils.MathUtil;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.utilities.MessageUtil;
import net.year4000.utilities.bukkit.ItemUtil;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class SpleefPowerUp implements Listener {
    private static final Random RAND = new Random();
    private Map<String, BukkitTask> powers = new HashMap<>();

    private static final String SPEED_NAME = "&a&lSpeed I";
    private static final ItemStack SPEED;
    static {
        SPEED = ItemUtil.makeItem("feather");
        ItemMeta meta = SPEED.getItemMeta();
        meta.setDisplayName(MessageUtil.replaceColors(SPEED_NAME));
        SPEED.setItemMeta(meta);
    }

    private static final String SPEED_II_NAME = "&a&lSpeed II";
    private static final ItemStack SPEED_II;
    static {
        SPEED_II = ItemUtil.makeItem("gold_boots");
        ItemMeta meta = SPEED_II.getItemMeta();
        meta.setDisplayName(MessageUtil.replaceColors(SPEED_II_NAME));
        SPEED_II.setItemMeta(meta);
    }

    private static final String TNT_NAME = "&a&lTNT";
    private static final ItemStack TNT;
    static {
        TNT = ItemUtil.makeItem("tnt");
        ItemMeta meta = TNT.getItemMeta();
        meta.setDisplayName(MessageUtil.replaceColors(TNT_NAME));
        TNT.setItemMeta(meta);
    }

    private static final ImmutableList<ItemStack> POWER_UPS = ImmutableList.<ItemStack>builder()
        .add(SPEED)
        .add(SPEED_II)
        .add(TNT)
        .build();


    /** Drop a random power up */
    public void dropPowerUp(Location location) {
        Item item = location.getWorld().dropItemNaturally(location, POWER_UPS.get(RAND.nextInt(POWER_UPS.size())));
        String name = item.getItemStack().getItemMeta().getDisplayName();

        // todo 3d hologram text above item
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        String name = event.getItem().getItemStack().getItemMeta().getDisplayName().replaceAll("§([0-9a-fA-Fk-rK-R])", "&$1");
        Player player = event.getPlayer();
        BukkitTask task = powers.get(player.getName());

        if (task != null) {
            task.cancel();
        }

        switch (name) {
            case SPEED_II_NAME: {
                int ticks = MathUtil.ticks(10);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, ticks, 1), true);
                player.sendMessage(Msg.locale(player, "spleef.power.given", SPEED_II_NAME, String.valueOf(MathUtil.sec(ticks))));
                powers.put(player.getName(), SchedulerUtil.runAsync(() -> player.sendMessage(Msg.locale(player, "spleef.power.expire", SPEED_II_NAME)), ticks));
                break;
            }
            case SPEED_NAME: {
                int ticks = MathUtil.ticks(5);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, ticks, 1), true);
                player.sendMessage(Msg.locale(player, "spleef.power.given", SPEED_NAME, String.valueOf(MathUtil.sec(ticks))));
                powers.put(player.getName(), SchedulerUtil.runAsync(() -> player.sendMessage(Msg.locale(player, "spleef.power.expire", SPEED_NAME)), ticks));
                break;
            }
            case TNT_NAME:
                player.getInventory().addItem(ItemUtil.makeItem("tnt", "{'display': {'name': '&aSpleef &6Runner'}}"));
                player.getInventory().addItem(ItemUtil.makeItem("tnt", "{'display': {'name': '&aSpleef &6Runner'}}"));
                player.sendMessage(Msg.locale(player, "spleef.tnt.received"));
                break;
        }

        player.updateInventory();
        event.setCancelled(true);
        event.getItem().remove();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo().toVector().toBlockVector().equals(event.getFrom().toVector().toBlockVector())) return;

        if (RAND.nextInt(50) == 5 && MapNodes.getCurrentGame().getStage().isPlaying()) {
            SchedulerUtil.runAsync(() -> dropPowerUp(Common.center(event.getFrom().clone())), 60L);
        }
    }
}
