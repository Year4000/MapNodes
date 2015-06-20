/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChestUtil {
    /** Is the chest empty. */
    public static boolean isEmpty(Chest chest) {
        for (ItemStack slot : chest.getInventory().getContents()) {
            if (slot != null) {
                return false;
            }
        }

        return true;
    }

    /** Is the inventory empty. */
    public static boolean isEmpty(Inventory inventory) {
        for (ItemStack slot : inventory.getContents()) {
            if (slot != null) {
                return false;
            }
        }

        return true;
    }
}
