/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.games.spleef;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.utils.Common;
import net.year4000.utilities.MessageUtil;
import net.year4000.utilities.bukkit.FunEffectsUtil;
import net.year4000.utilities.bukkit.LocationUtil;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.EulerAngle;

import java.util.List;
import java.util.function.BiConsumer;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class PowerUp {
    public static final List<PowerUp> powerups = Lists.newCopyOnWriteArrayList();
    @Getter
    private final Color color;
    @Getter
    private final String display;
    @Getter
    private final Material icon;
    private final BiConsumer<GamePlayer, PowerUp> action;
    private final boolean copy;
    private ArmorStand stand;
    private int x, y, z;
    private double step = 0;

    /** Create the power up */
    public PowerUp(String display, Material icon, Color color, BiConsumer<GamePlayer, PowerUp> action) {
        this.display = MessageUtil.replaceColors(checkNotNull(display));
        this.color = checkNotNull(color);
        this.icon = checkNotNull(icon);
        this.action = checkNotNull(action);
        this.copy = false;
    }

    /** Create the power up */
    private PowerUp(boolean copy, String display, Material icon, Color color, BiConsumer<GamePlayer, PowerUp> action) {
        this.display = MessageUtil.replaceColors(checkNotNull(display));
        this.color = checkNotNull(color);
        this.icon = checkNotNull(icon);
        this.action = checkNotNull(action);
        this.copy = copy;
    }

    /** Create the power up instance at the current location */
    public PowerUp createPowerUp(Location location) {
        PowerUp powerUp = new PowerUp(true, display, icon, color, action);
        Location center = LocationUtil.center(location);
        // Set locations
        powerUp.x = center.getBlockX();
        powerUp.y = center.getBlockY();
        powerUp.z = center.getBlockZ();

        // Spawn armor stand
        powerUp.stand = (ArmorStand) MapNodes.getCurrentWorld().spawnEntity(center.subtract(0, 0.5, 0), EntityType.ARMOR_STAND);
        powerUp.stand.setHelmet(new ItemStack(powerUp.icon));
        powerUp.stand.setVisible(false);
        powerUp.stand.setHeadPose(new EulerAngle(0, 0, 0));
        powerUp.stand.setSmall(true);
        powerUp.stand.setGravity(false);

        // Set display name
        powerUp.stand.setCustomName(powerUp.display);
        powerUp.stand.setCustomNameVisible(true);

        // Show firework
        Firework firework = MapNodes.getCurrentWorld().spawn(center, Firework.class);
        FireworkEffect effect = FireworkEffect.builder()
            .withColor(powerUp.color)
            .flicker(true)
            .trail(true)
            .with(FireworkEffect.Type.BALL)
            .build();
        FireworkMeta meta = firework.getFireworkMeta();
        meta.clearEffects();
        meta.addEffect(effect);
        meta.setPower(1);
        firework.setFireworkMeta(meta);

        // Broadcast
        MapNodes.getCurrentGame().getPlaying().forEach(player -> {
            Common.sendAnimatedActionBar(player, "&6Power Up&7: " + powerUp.display);
            FunEffectsUtil.playSound(player.getPlayer(), Sound.ENDERMAN_SCREAM);
        });

        powerups.add(powerUp);

        return powerUp;
    }

    /** Run the action tided to power up instance */
    public void run(GamePlayer player) {
        checkState(copy, "not a copy");
        player.sendMessage(MessageUtil.replaceColors("&b+10 &6tokens"));
        MapNodesPlugin.getInst().getApi().addTokens(player, 10);
        player.addMultiplierModifier();
        action.accept(player, this);
        stand.remove();
        powerups.remove(this);

        FunEffectsUtil.playSound(player.getPlayer(), Sound.ITEM_PICKUP);
    }

    /** Can the location pick up the power up */
    public boolean canPickUp(Location location) {
        checkState(copy, "not a copy");

        return location.getBlockX() == x && location.getBlockY() == y && location.getBlockZ() == z;
    }

    /** Rotate the power up head */
    public void rotate() {
        step = step >= 360 ? 0 : step + 0.15;
        stand.setHeadPose(new EulerAngle(0, step, 0));
    }
}
