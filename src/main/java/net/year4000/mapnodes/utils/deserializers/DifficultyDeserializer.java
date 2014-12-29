package net.year4000.mapnodes.utils.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.messages.Msg;
import org.bukkit.Difficulty;

import java.lang.reflect.Type;

public class DifficultyDeserializer implements JsonDeserializer<Difficulty> {
    @Override
    public Difficulty deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        String name = element.getAsString();

        try {
            return Difficulty.valueOf(name.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            MapNodesPlugin.debug(Msg.util("settings.difficulty", name));
        }

        return Difficulty.NORMAL;
    }
}
