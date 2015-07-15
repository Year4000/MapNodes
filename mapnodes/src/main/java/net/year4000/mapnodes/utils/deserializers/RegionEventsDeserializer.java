/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.utils.deserializers;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import lombok.AllArgsConstructor;
import net.year4000.mapnodes.api.game.regions.RegionListener;
import net.year4000.mapnodes.api.game.regions.EventTypes;
import net.year4000.mapnodes.game.regions.EventManager;
import net.year4000.mapnodes.game.regions.RegionEvents;
import net.year4000.mapnodes.utils.GsonUtil;
import org.bukkit.World;

import java.lang.reflect.Type;
import java.util.Map;

@AllArgsConstructor
public class RegionEventsDeserializer implements JsonDeserializer<RegionEvents> {
    private World world;

    @Override
    public RegionEvents deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        Gson gson = GsonUtil.createGson(world);

        Map<String, JsonObject> map = gson.fromJson(element, new TypeToken<Map<String, JsonObject>>() {
        }.getType());
        RegionEvents events = new RegionEvents();

        map.forEach((name, object) -> {
            RegionListener listener = gson.fromJson(object, EventManager.get().getRegionType(name));
            // MapNodesPlugin.debug("Loading region event: " + listener.getClass().getSimpleName());
            events.addEvent(listener, EventTypes.getFromName(name));
        });

        return events;
    }
}