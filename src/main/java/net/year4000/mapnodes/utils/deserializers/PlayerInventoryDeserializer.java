/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.utils.deserializers;

import com.google.gson.*;
import net.year4000.mapnodes.game.kits.SlotItem;
import net.year4000.mapnodes.utils.GsonUtil;
import net.year4000.mapnodes.utils.typewrappers.PlayerInventoryList;
import net.year4000.utilities.bukkit.ItemUtil;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.List;

public class PlayerInventoryDeserializer implements JsonDeserializer<List<ItemStack>> {
    @Override
    public List<ItemStack> deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        PlayerInventoryList<ItemStack> newList = new PlayerInventoryList<>();
        Gson gson = GsonUtil.createGson();

        for (int i = 0; i < 36; i++) {
            newList.add(ItemUtil.makeItem("air"));
        }

        for (JsonElement item : element.getAsJsonArray()) {
            SlotItem itemSlot = gson.fromJson(item, SlotItem.class);
            newList.getRawItems().add(itemSlot);
            newList.set(itemSlot.getSlot() == -1 ? 0 : itemSlot.getSlot(), itemSlot.create());
        }

        return newList;
    }
}
