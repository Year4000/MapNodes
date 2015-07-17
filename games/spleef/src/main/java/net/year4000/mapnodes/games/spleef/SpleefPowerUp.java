/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.games.spleef;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.events.game.GameStartEvent;
import net.year4000.mapnodes.api.events.game.GameStopEvent;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.*;
import net.year4000.utilities.bukkit.ItemUtil;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;

public class SpleefPowerUp implements Listener {
    private final Random RAND = new Random();
    private Set<Location> positions = Sets.newHashSet();
    private Optional<BukkitTask> notification = Optional.empty();
    private Optional<BukkitTask> heads = Optional.empty();
    private Optional<BukkitTask> powers = Optional.empty();

    private final ImmutableList<PowerUp> POWER_UPS = ImmutableList.<PowerUp>builder()
        .add(new PowerUp("&a&lSpeed I", Material.WOOL, Color.WHITE, (player, power) -> {
            int ticks = MathUtil.ticks(5);
            player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, ticks, 1), true);
            player.sendMessage(Msg.locale(player, "spleef.power.given", power.getDisplay(), String.valueOf(MathUtil.sec(ticks))));
            UserData data = player.getPlayerData(UserData.class);
            data.setAmount(1);
            data.addSpeed("spleef.power.expire", power.getDisplay(), ticks);
        }))
        .add(new PowerUp("&a&lSpeed II", Material.GOLD_BLOCK, Color.ORANGE, (player, power) -> {
            int ticks = MathUtil.ticks(10);
            player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, ticks, 1), true);
            player.sendMessage(Msg.locale(player, "spleef.power.given", power.getDisplay(), String.valueOf(MathUtil.sec(ticks))));
            UserData data = player.getPlayerData(UserData.class);
            data.setAmount(1);
            data.addSpeed("spleef.power.expire", power.getDisplay(), ticks);

        }))
        .add(new PowerUp("&a&lTNT", Material.TNT, Color.RED, (player, power) -> {
            player.getPlayer().getInventory().addItem(ItemUtil.makeItem("tnt", "{'display': {'name': '&aSpleef &6Runner'}}"));
            player.getPlayer().getInventory().addItem(ItemUtil.makeItem("tnt", "{'display': {'name': '&aSpleef &6Runner'}}"));
            player.sendMessage(Msg.locale(player, "spleef.tnt.received"));
            UserData data = player.getPlayerData(UserData.class);
            data.setAmount(1);
        }))
        .add(new PowerUp("&a&lTri Power", Material.GLOWSTONE, Color.AQUA, (player, power) -> {
            UserData data = player.getPlayerData(UserData.class);
            data.setAmount(3);
            player.sendMessage(Msg.locale(player, "spleef.tri_power.activated"));
        }))
        .add(new PowerUp("&a&lBi Power", Material.SEA_LANTERN, Color.YELLOW, (player, power) -> {
            UserData data = player.getPlayerData(UserData.class);
            data.setAmount(2);
            player.sendMessage(Msg.locale(player, "spleef.bi_power.activated"));
        }))
        .add(new PowerUp("&a&lEnder Power", Material.ENDER_STONE, Color.NAVY, (player, power) -> {
            player.getPlayer().getInventory().addItem(ItemUtil.makeItem("ender_pearl"));
            player.sendMessage(Msg.locale(player, "spleef.ender.received"));
            UserData data = player.getPlayerData(UserData.class);
            data.setAmount(1);
        }))
        .build();

    /** Method to create power up */
    public void createPowerUp(Location location) {
        PowerUp powerUp = POWER_UPS.get(RAND.nextInt(POWER_UPS.size()));
        Location floor = location.clone();
        while (floor.getBlock().isEmpty() && floor.getY() > 0) {
            floor.subtract(0, 1, 0);
        }

        if (floor.getY() == 0) {
            positions.remove(location);
        }
        else {
            powerUp.createPowerUp(floor.add(0, 1, 0));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (!MapNodes.getCurrentGame().getStage().isPlaying()) return;
        if (event.getTo().toVector().toBlockVector().equals(event.getFrom().toVector().toBlockVector())) return;
        Location from = event.getFrom();

        // Find power up and pick it up
        PowerUp.powerups.stream()
            .filter(power -> power.canPickUp(event.getTo()))
            .findFirst()
            .ifPresent(power -> power.run(player));

        // Add location to positions to spawn power ups
        positions.add(from);
    }

    @EventHandler
    public void onStop(GameStartEvent event) {
        heads = Optional.of(SchedulerUtil.repeatSync(() -> PowerUp.powerups.forEach(PowerUp::rotate), 2));
        SchedulerUtil.runSync(() -> {
            powers = Optional.of(SchedulerUtil.repeatSync(() -> {
                List<Location> locations = positions.stream().collect(Collectors.toList());
                Collections.shuffle(locations);
                locations.stream().findAny().ifPresent(this::createPowerUp);
            }, 20 * 15));
            notification = Optional.of(SchedulerUtil.repeatSync(() -> {
                MapNodes.getCurrentGame().getPlaying().forEach(player -> {
                    int last = player.getPlayer().getLevel();
                    if (last <= 1) {
                        player.getPlayer().setExp(1);
                        player.getPlayer().setLevel(15);
                    }
                    else {
                        player.getPlayer().setLevel(last - 1);
                        player.getPlayer().setExp(0);
                    }
                });
            }, 20));
        }, 20 * 5);
    }

    @EventHandler
    public void onStop(GameStopEvent event) {
        heads.ifPresent(BukkitTask::cancel);
        powers.ifPresent(BukkitTask::cancel);
        notification.ifPresent(BukkitTask::cancel);
    }
}
