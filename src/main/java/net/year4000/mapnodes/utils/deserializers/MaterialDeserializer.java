package net.year4000.mapnodes.utils.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.messages.Msg;
import org.bukkit.Material;

import java.lang.reflect.Type;

public class MaterialDeserializer implements JsonDeserializer<Material> {
    @Override
    public Material deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        String name = element.getAsString();

        try {
            return Material.valueOf(name.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            MapNodesPlugin.debug(Msg.util("settings.material", name));
        }

        return null;
    }
}
