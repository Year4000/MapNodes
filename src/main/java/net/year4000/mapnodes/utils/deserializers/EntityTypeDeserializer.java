package net.year4000.mapnodes.utils.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.messages.Msg;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Type;

public class EntityTypeDeserializer implements JsonDeserializer<EntityType> {
    @Override
    public EntityType deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        String name = element.getAsString();

        try {
            return EntityType.valueOf(name.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            MapNodesPlugin.debug(Msg.util("settings.entitytype", name));
        }

        return null;
    }
}
