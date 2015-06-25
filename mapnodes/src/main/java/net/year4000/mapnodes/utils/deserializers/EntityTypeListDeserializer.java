/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.utils.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.typewrappers.EntityTypeList;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class EntityTypeListDeserializer implements JsonDeserializer<List<EntityType>> {
    @Override
    public List<EntityType> deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        List<EntityType> newList = new EntityTypeList<>();

        for (JsonElement item : element.getAsJsonArray()) {
            String itemName = item.getAsString();

            if (itemName.equalsIgnoreCase("all")) {
                Arrays.asList(EntityType.values()).forEach(newList::add);
                return newList;
            }

            try {
                newList.add(EntityType.valueOf(itemName.toUpperCase()));
            }
            catch (IllegalArgumentException e) {
                MapNodesPlugin.debug(Msg.util("settings.entitytype", itemName));
            }
        }

        return newList;
    }
}
