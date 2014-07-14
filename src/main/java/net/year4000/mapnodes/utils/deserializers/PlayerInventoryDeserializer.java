package net.year4000.mapnodes.utils.deserializers;

import com.ewized.utilities.bukkit.util.ItemUtil;
import com.google.gson.*;
import net.year4000.mapnodes.game.components.kits.SlotItem;
import net.year4000.mapnodes.utils.GsonUtil;
import net.year4000.mapnodes.utils.typewrappers.PlayerInventoryList;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.List;

public class PlayerInventoryDeserializer implements JsonDeserializer<List<ItemStack>> {
    @Override
    public List<ItemStack> deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        List<ItemStack> newList = new PlayerInventoryList<>();
        Gson gson = GsonUtil.createGson();

        for (int i = 0; i < 36; i++) {
            newList.add(ItemUtil.makeItem("air"));
        }

        for (JsonElement item : element.getAsJsonArray()) {
            SlotItem itemSlot = gson.fromJson(item, SlotItem.class);
            newList.set(itemSlot.getSlot(), itemSlot.create());
        }

        return newList;
    }
}
