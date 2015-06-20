/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.utils.deserializers;

import com.google.gson.*;
import net.year4000.mapnodes.game.kits.Item;
import net.year4000.mapnodes.utils.GsonUtil;
import net.year4000.mapnodes.utils.typewrappers.ItemStackList;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.List;

public class ItemListDeserializer implements JsonDeserializer<List<ItemStack>> {
    @Override
    public List<ItemStack> deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        List<ItemStack> newList = new ItemStackList<>();
        Gson gson = GsonUtil.createGson();

        for (JsonElement item : element.getAsJsonArray()) {
            Item itemSlot = gson.fromJson(item, Item.class);
            newList.add(itemSlot.create());
        }

        return newList;
    }
}
