/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.game.kits;

import net.year4000.mapnodes.api.exceptions.InvalidJsonException;
import net.year4000.mapnodes.api.utils.Validator;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.GsonUtil;
import net.year4000.utilities.bukkit.ItemUtil;
import net.year4000.utilities.bukkit.items.NBT;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Item implements Validator {
    /** The amount of items to give. */
    private int amount = 1;

    /** The name of the item. */
    private Material item = null;

    /** The damage of the item. */
    private Short damage = 0;

    /** NBT data of the item. */
    private NBT nbt = null;

    public Item() {
    }

    @Override
    public void validate() throws InvalidJsonException {
        if (amount < 1) {
            throw new InvalidJsonException(Msg.util("settings.kit.item.amount"));
        }

        if (item == null) {
            throw new InvalidJsonException(Msg.util("settings.kit.item.item"));
        }
    }

    /*//--------------------------------------------//
         Upper Json Settings / Bellow Instance Code
    *///--------------------------------------------//

    /** Create the item based on this class */
    public ItemStack create() {
        ItemStack itemStack = new ItemStack(item, amount, damage);

        // Only add nbt if the items has nbt
        if (nbt != null) {
            itemStack.setItemMeta(ItemUtil.addMeta(itemStack, GsonUtil.GSON.toJson(nbt)));
        }

        return itemStack;
    }

    public int getAmount() {
        return this.amount;
    }

    public Material getItem() {
        return this.item;
    }

    public Short getDamage() {
        return this.damage;
    }

    public NBT getNbt() {
        return this.nbt;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setItem(Material item) {
        this.item = item;
    }

    public void setDamage(Short damage) {
        this.damage = damage;
    }

    public void setNbt(NBT nbt) {
        this.nbt = nbt;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Item)) return false;
        final Item other = (Item) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.getAmount() != other.getAmount()) return false;
        final Object this$item = this.getItem();
        final Object other$item = other.getItem();
        if (this$item == null ? other$item != null : !this$item.equals(other$item)) return false;
        final Object this$damage = this.getDamage();
        final Object other$damage = other.getDamage();
        if (this$damage == null ? other$damage != null : !this$damage.equals(other$damage)) return false;
        final Object this$nbt = this.getNbt();
        final Object other$nbt = other.getNbt();
        if (this$nbt == null ? other$nbt != null : !this$nbt.equals(other$nbt)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getAmount();
        final Object $item = this.getItem();
        result = result * PRIME + ($item == null ? 43 : $item.hashCode());
        final Object $damage = this.getDamage();
        result = result * PRIME + ($damage == null ? 43 : $damage.hashCode());
        final Object $nbt = this.getNbt();
        result = result * PRIME + ($nbt == null ? 43 : $nbt.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Item;
    }

    public String toString() {
        return "net.year4000.mapnodes.game.kits.Item(amount=" + this.getAmount() + ", item=" + this.getItem() + ", damage=" + this.getDamage() + ", nbt=" + this.getNbt() + ")";
    }
}
