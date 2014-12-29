package net.year4000.mapnodes.game.kits;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.year4000.mapnodes.api.exceptions.InvalidJsonException;
import net.year4000.mapnodes.api.utils.Validator;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.GsonUtil;
import net.year4000.utilities.bukkit.ItemUtil;
import net.year4000.utilities.bukkit.items.NBT;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Data
@NoArgsConstructor
public class Item implements Validator {
    /** The amount of items to give. */
    private int amount = 1;

    /** The name of the item. */
    private Material item = null;

    /** The damage of the item. */
    private Short damage = 0;

    /** NBT data of the item. */
    private NBT nbt = null;

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
}
