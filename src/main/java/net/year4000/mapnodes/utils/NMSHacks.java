package net.year4000.mapnodes.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.server.v1_7_R4.EntityLiving;
import net.minecraft.server.v1_7_R4.EntityProjectile;
import net.minecraft.server.v1_7_R4.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftProjectile;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftTNTPrimed;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_7_R4.scoreboard.CraftTeam;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NMSHacks {
    /** Is the team registered */
    public static boolean isTeamRegistered(Team team) {
        return team instanceof CraftTeam && ((CraftTeam) team).getScoreboard() != null;
    }

    /** Get the NBT Tag is their is one */
    public static String getNBTTag(ItemStack item, String key) {
        net.minecraft.server.v1_7_R4.ItemStack craftItem = CraftItemStack.asNMSCopy(item);

        if (!craftItem.hasTag()) {
            return null;
        }

        return craftItem.getTag().getString(key);
    }

    /** Set a custom NBT data to an item */
    public static void setNBTTag(ItemStack item, String key, String value) {
        net.minecraft.server.v1_7_R4.ItemStack craftItem = CraftItemStack.asNMSCopy(item);

        if (!craftItem.hasTag()) {
            craftItem.setTag(new NBTTagCompound());
        }

        craftItem.getTag().setString(key, value);
    }

    /** Add the source to the tnt */
    public static void addTNTSource(Entity entity, LivingEntity source) {
        ((CraftTNTPrimed) entity).getHandle().projectileSource = source;
    }

    /** Add a shooter to the projectile */
    public static void addShooter(Entity entity, LivingEntity shooter) {
        EntityProjectile projectile = ((CraftProjectile) entity).getHandle();
        projectile.shooter = (EntityLiving) shooter;
        projectile.shooterName = ((EntityLiving) shooter).getName();
    }

    /** Create an explosion based off an entity */
    public static void createExplosion(Entity entity, Location location, byte power, boolean fire, boolean breakBlocks) {
        net.minecraft.server.v1_7_R4.Entity owner = entity == null ? null : ((CraftEntity) entity).getHandle();
        ((CraftWorld) location.getWorld()).getHandle().createExplosion(owner, location.getX(), location.getY(), location.getZ(), power, fire, breakBlocks);
    }
}
