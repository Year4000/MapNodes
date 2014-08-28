package net.year4000.mapnodes.utils.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.typewrappers.DamageCauseList;
import org.bukkit.event.entity.EntityDamageEvent;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class DamageCauseListDeserializer implements JsonDeserializer<List<EntityDamageEvent.DamageCause>> {
    @Override
    public List<EntityDamageEvent.DamageCause> deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        List<EntityDamageEvent.DamageCause> newList = new DamageCauseList<>();

        for (JsonElement item : element.getAsJsonArray()) {
            String itemName = item.getAsString();

            if (itemName.equalsIgnoreCase("all")) {
                Arrays.asList(EntityDamageEvent.DamageCause.values()).forEach(newList::add);
                return newList;
            }

            try {
                newList.add(EntityDamageEvent.DamageCause.valueOf(itemName.toUpperCase()));
            } catch (IllegalArgumentException e) {
                MapNodesPlugin.debug(Msg.util("settings.damagecause"), itemName);
            }
        }

        return newList;
    }
}
