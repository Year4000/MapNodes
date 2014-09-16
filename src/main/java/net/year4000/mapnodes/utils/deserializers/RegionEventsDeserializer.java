package net.year4000.mapnodes.utils.deserializers;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.game.regions.EventManager;
import net.year4000.mapnodes.game.regions.EventTypes;
import net.year4000.mapnodes.game.regions.RegionEvents;
import net.year4000.mapnodes.utils.GsonUtil;
import org.bukkit.event.Listener;

import java.lang.reflect.Type;
import java.util.Map;

public class RegionEventsDeserializer implements JsonDeserializer<RegionEvents> {
    @Override
    public RegionEvents deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        Map<String, JsonObject> map = GsonUtil.GSON.fromJson(element, new TypeToken<Map<String, JsonObject>>(){}.getType());
        RegionEvents events = new RegionEvents();

        map.forEach((name, object) -> {
            Listener listener = GsonUtil.GSON.fromJson(object, EventManager.get().getRegionType(name));
            MapNodesPlugin.debug("Loading region event: " + listener.getClass().getSimpleName());
            events.addEvent(listener, EventTypes.getFromName(name));
        });

        return events;
    }
}
