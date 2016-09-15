/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.utils;

import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityProjectile;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.year4000.utilities.bukkit.ItemUtil;
import net.year4000.utilities.bukkit.items.NBT;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftProjectile;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftTNTPrimed;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scoreboard.Team;

import static com.google.common.base.Preconditions.checkArgument;

public final class NMSHacks {
    public NMSHacks() {
    }

    /** Is the team registered */
    public static boolean isTeamRegistered(Team team) {
        return team.getScoreboard() != null;
    }

    /** Get the NBT Tag is their is one */
    public static String getNBTTag(ItemStack item, String key) {
        net.minecraft.server.v1_8_R3.ItemStack craftItem = CraftItemStack.asNMSCopy(item);

        if (!craftItem.hasTag()) {
            return null;
        }

        return craftItem.getTag().getString(key);
    }

    /** Set a custom NBT data to an item */
    public static void setNBTTag(ItemStack item, String key, String value) {
        net.minecraft.server.v1_8_R3.ItemStack craftItem = CraftItemStack.asNMSCopy(item);

        if (!craftItem.hasTag()) {
            craftItem.setTag(new NBTTagCompound());
        }

        craftItem.getTag().setString(key, value);
    }

    /** Add the source to the tnt */
    public static void addTNTSource(Entity entity, LivingEntity source) {
        ((CraftTNTPrimed) entity).getHandle().projectileSource = source;
    }

    /** Add the source to the tnt */
    public static LivingEntity getTNTSource(Entity entity) {
        return (LivingEntity) ((CraftTNTPrimed) entity).getHandle().projectileSource;
    }

    /** Add a shooter to the projectile */
    public static void addShooter(Entity entity, LivingEntity shooter) {
        EntityProjectile projectile = ((CraftProjectile) entity).getHandle();
        projectile.shooter = (EntityLiving) shooter;
        projectile.shooterName = shooter.getName();
    }

    /** Create an explosion based off an entity */
    public static void createExplosion(Entity entity, Location location, byte power, boolean fire, boolean breakBlocks) {
        net.minecraft.server.v1_8_R3.Entity owner = entity == null ? null : ((CraftEntity) entity).getHandle();
        ((CraftWorld) location.getWorld()).getHandle().createExplosion(owner, location.getX(), location.getY(), location.getZ(), power, fire, breakBlocks);
    }

    /** Make and set the ItemStack for the player skull */
    public static ItemStack makeSkull(Player player) {
        return makeSkull(player, player.getName());
    }

    /** Make and set the ItemStack for the player skull */
    public static ItemStack makeSkull(Player player, String display) {
        NBT nbt = GsonUtil.GSON.fromJson("{'display':{}}", NBT.class);
        nbt.getDisplay().setName(display == null ? player.getName() : display);
        ItemStack head = ItemUtil.makeItem("skull_item", 1, (short) 3);
        head.setItemMeta(ItemUtil.addMeta(head, GsonUtil.GSON.toJson(nbt)));

        return head;
    }

    /** Set the skull skin from player object */
    public static ItemStack setSkullSkin(ItemStack skull, Player player) {
        checkArgument(skull.getType() == Material.SKULL_ITEM);
        checkArgument(skull.getData().getData() == 3);

        ItemStack copy = skull.clone();
        SkullMeta meta = (SkullMeta) copy.getItemMeta();
        meta.setOwner(player.getName());
        copy.setItemMeta(meta);

        return copy;
    }
}
