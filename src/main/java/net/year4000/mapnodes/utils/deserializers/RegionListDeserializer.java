package net.year4000.mapnodes.utils.deserializers;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.game.regions.Region;
import net.year4000.mapnodes.game.regions.RegionManager;
import net.year4000.mapnodes.utils.GsonUtil;
import net.year4000.mapnodes.utils.typewrappers.RegionList;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class RegionListDeserializer implements JsonDeserializer<List<Region>> {
    @Override
    public List<Region> deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        Gson gson = GsonUtil.createGson();
        List<Region> newList = new RegionList<>();

        for (JsonElement rawRegion : element.getAsJsonArray()) {
            // Create a hashmap with one element to act like {"": {}}
            Map<String, JsonObject> map = gson.fromJson(rawRegion, new TypeToken<Map<String, JsonObject>>(){}.getType());

            map.forEach((key, value) -> {
                try {
                    newList.add(gson.fromJson(value, RegionManager.get().getRegionType(key)));
                } catch (Exception e) {
                    MapNodesPlugin.log(e, true);
                }
            });
        }

        return newList;
    }
}

