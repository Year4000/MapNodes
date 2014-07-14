package net.year4000.mapnodes.utils.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.LogUtil;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Type;

public class PotionEffectDeserializer implements JsonDeserializer<PotionEffectType> {
    @Override
    public PotionEffectType deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        String name = element.getAsString();

        PotionEffectType potion = PotionEffectType.getByName(name.toUpperCase());

        if (potion == null) {
            LogUtil.debug(Msg.util("settings.kit.effect.name"), name);
        }

        return potion;
    }
}
