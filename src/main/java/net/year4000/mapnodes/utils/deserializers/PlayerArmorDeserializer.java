package net.year4000.mapnodes.utils.deserializers;

import com.ewized.utilities.bukkit.util.ItemUtil;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.year4000.mapnodes.game.components.kits.Armor;
import net.year4000.mapnodes.utils.GsonUtil;
import net.year4000.mapnodes.utils.typewrappers.PlayerArmorList;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.List;

public class PlayerArmorDeserializer implements JsonDeserializer<List<ItemStack>> {
    @Override
    public List<ItemStack> deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        List<ItemStack> newList = new PlayerArmorList<>();

        for (int i = 0; i < 4; i++) {
            newList.add(ItemUtil.makeItem("air"));
        }

        Armor armor = GsonUtil.createGson().fromJson(element.getAsJsonObject(), Armor.class);

        newList.set(3, armor.getHelmet().create());
        newList.set(2, armor.getChestplate().create());
        newList.set(1, armor.getLeggings().create());
        newList.set(0, armor.getBoots().create());

        return newList;
    }
}
