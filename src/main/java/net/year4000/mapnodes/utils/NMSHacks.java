package net.year4000.mapnodes.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.server.v1_7_R4.EntityLiving;
import net.minecraft.server.v1_7_R4.EntityProjectile;
import net.minecraft.server.v1_7_R4.NBTTagCompound;
import net.minecraft.server.v1_7_R4.NBTTagList;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.utilities.bukkit.ItemUtil;
import net.year4000.utilities.bukkit.items.NBT;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftProjectile;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftTNTPrimed;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_7_R4.scoreboard.CraftTeam;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

import static com.google.common.base.Preconditions.checkArgument;

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

        net.minecraft.server.v1_7_R4.ItemStack nms = CraftItemStack.asNMSCopy(skull.clone());
        NBTTagCompound tag = nms.getTag();
        NBTTagCompound owner = new NBTTagCompound();
        NBTTagCompound prop = new NBTTagCompound();
        NBTTagList textures = new NBTTagList();
        NBTTagCompound skin = new NBTTagCompound();

        owner.setString("Id", player.getUniqueId().toString());
        owner.setString("Name", player.getName());
        skin.setString("Value", player.getSkin().getData());
        skin.setString("Signature", player.getSkin().getSignature());
        textures.add(skin);
        prop.set("textures", textures);
        owner.set("Properties", prop);
        tag.set("SkullOwner", owner);
        nms.setTag(tag);

        return CraftItemStack.asBukkitCopy(nms);
    }
}
