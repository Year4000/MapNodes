package net.year4000.mapnodes.utils;

import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class ChestUtil {
    /** Is the chest empty. */
    public static boolean isEmpty(Chest chest) {
        for (ItemStack slot : chest.getInventory().getContents()) {
            if (slot != null) return false;
        }
        return true;
    }

    /** Is the chest empty. */
    public static boolean isEmpty(Inventory inventory) {
        for (ItemStack slot : inventory.getContents()) {
            if (slot != null) return false;
        }
        return true;
    }
}
