package net.year4000.mapnodes.utils.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.year4000.mapnodes.game.components.regions.Point;
import net.year4000.mapnodes.game.components.regions.Region;
import net.year4000.mapnodes.utils.GsonUtil;
import net.year4000.mapnodes.utils.typewrappers.LocationList;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Type;
import java.util.List;

public class LocationListDesterializer implements JsonDeserializer<List<Location>> {
    private World world;

    public LocationListDesterializer(World world) {
        this.world = world;
    }

    @Override
    public List<Location> deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        List<Location> newList = new LocationList<>();

        for (JsonElement rawRegion : element.getAsJsonArray()) {

            Region region = GsonUtil.createGson(world).fromJson(rawRegion, Point.class);

            newList.addAll(region.getLocations(world));
        }

        return newList;
    }
}
