package net.year4000.mapnodes.utils.deserializers;

import com.google.gson.*;
import net.year4000.mapnodes.game.components.kits.Effect;
import net.year4000.mapnodes.utils.GsonUtil;
import net.year4000.mapnodes.utils.typewrappers.PotionEffectList;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.Type;
import java.util.List;

public class PotionEffectListDeserializer implements JsonDeserializer<List<PotionEffect>> {
    @Override
    public List<PotionEffect> deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        List<PotionEffect> newList = new PotionEffectList<>();
        Gson gson = GsonUtil.createGson();

        for (JsonElement item : element.getAsJsonArray()) {
            newList.add(gson.fromJson(item, Effect.class).makeEffect());
        }

        return newList;
    }
}
