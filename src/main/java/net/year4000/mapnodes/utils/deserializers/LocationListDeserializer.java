package net.year4000.mapnodes.utils.deserializers;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import lombok.AllArgsConstructor;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.api.game.regions.Region;
import net.year4000.mapnodes.game.regions.RegionManager;
import net.year4000.mapnodes.utils.GsonUtil;
import net.year4000.mapnodes.utils.typewrappers.LocationList;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class LocationListDeserializer implements JsonDeserializer<List<Location>> {
    private World world;

    @Override
    public List<Location> deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        List<Location> newList = new LocationList<>();

        for (JsonElement rawRegion : element.getAsJsonArray()) {
            // Create a hashmap with one element to act like {"": {}}
            Map<String, JsonObject> map = GsonUtil.GSON.fromJson(rawRegion, new TypeToken<Map<String, JsonObject>>() {
            }.getType());

            map.forEach((key, value) -> {
                try {
                    Region region = GsonUtil.createGson(world).fromJson(value, RegionManager.get().getRegionType(key));
                    newList.addAll(region.getLocations(world));
                }
                catch (Exception e) {
                    MapNodesPlugin.log(e, true);
                }
            });
        }

        return newList;
    }
}
