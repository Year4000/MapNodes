/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.games.skywars;

import com.google.common.collect.Lists;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.events.game.GameLoadEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerStartEvent;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.game.NodeClass;
import net.year4000.mapnodes.game.NodeGame;
import net.year4000.mapnodes.game.NodeKit;
import net.year4000.mapnodes.games.elimination.Elimination;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.utilities.bukkit.ItemUtil;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@GameModeInfo(
    name = "Skywars",
    version = "1.2",
    config = SkywarsConfig.class
)
public class Skywars extends Elimination {
    private Map<String, Integer> kills = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void onLoadSkyWars(GameLoadEvent event) {
        NodeGame game = ((NodeGame) event.getGame());
        game.loadClass("skywars_enderman", new NodeClass(game, "Enderman", Material.ENDER_PEARL, "skywars.enderman", "skywars_enderman", "theta"));
        game.loadClass("skywars_archer", new NodeClass(game, "Archer", Material.BOW, "skywars.archer", "skywars_archer", "mu"));
        game.loadClass("skywars_runner", new NodeClass(game, "Runner", Material.GOLD_BOOTS, "skywars.runner", "skywars_runner", "mu"));
        game.loadClass("skywars_heavy", new NodeClass(game, "Heavy", Material.LEATHER_CHESTPLATE, "skywars.heavy", "skywars_heavy", "pi"));
        game.loadClass("skywars_demoman", new NodeClass(game, "Demoman", Material.TNT, "skywars.demoman", "skywars_demoman", "sigma"));
        game.loadClass("skywars_jumper", new NodeClass(game, "Jumper", Material.SLIME_BALL, "skywars.jumper", "skywars_jumper", "theta"));
        game.addTask(SchedulerUtil.repeatSync(Tracker.TrackerRunner::new, 1));
    }

    // todo After x time go to sudden death and start blowing up islands

    @EventHandler
    public void onStart(GamePlayerStartEvent event) {
        List<ItemStack> items = Lists.newArrayList(
            new ItemStack(Material.WOOD_SWORD),
            new ItemStack(Material.WOOD_AXE),
            new ItemStack(Material.WOOD_PICKAXE),
            new ItemStack(Material.WOOD_SPADE)
        );
        NodeKit basic = new NodeKit();
        basic.getItems().addAll(items);
        basic.addKit(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFire(BlockSpreadEvent event) {
        if (event.getSource().getType() == Material.FIRE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTNTLaunch(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        boolean rightAir = event.getAction() == Action.RIGHT_CLICK_AIR;
        boolean rightBlock = event.getAction() == Action.RIGHT_CLICK_BLOCK;
        boolean material = event.getMaterial() == Material.TNT;

        if (rightAir && material) {
            Location location = player.getEyeLocation().clone().subtract(0, 0.25, 0);
            Entity tnt = MapNodes.getCurrentWorld().spawnEntity(location, EntityType.PRIMED_TNT);
            new Tracker(MapNodes.getCurrentWorld(), tnt.getEntityId(), Effect.PARTICLE_SMOKE, 0);
            tnt.setVelocity(location.getDirection().normalize().multiply(1.75));
            ItemStack tntStack = player.getItemInHand();

            if (tntStack.getAmount() > 1) {
                tntStack.setAmount(tntStack.getAmount() - 1);
            }
            else {
                player.setItemInHand(ItemUtil.makeItem("air"));
            }
        }
        else if (rightBlock && material) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFire(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onKill(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();

        if (killer != null) {
            kills.put(killer.getName(), kills.getOrDefault(killer.getName(), 0) + 1);
        }
    }

    /** Build the sidebar and send it to the players */
    @Override
    public void buildAndSendList() {
        game.getSidebarGoals().clear();
        int total = alive.size() + dead.size();

        if (total > 15) {
            game.addDynamicGoal("alive", MessageUtil.replaceColors("&6Alive&7:"), alive.size());
            game.addDynamicGoal("dead", MessageUtil.replaceColors("&6Dead&7:"), dead.size());
        }
        else {
            alive.stream()
                .sorted((l, r) -> kills.getOrDefault(l, 0) < kills.getOrDefault(r, 0) ? 1 : -1)
                .forEach(name -> game.addStaticGoal(name, "&7(&e" + kills.getOrDefault(name, 0) + "&7) &a" + name));
            dead.forEach(name -> game.addStaticGoal(name, "&7(&e" + kills.getOrDefault(name, 0) + "&7) &c&m" + name));
        }
    }
}
