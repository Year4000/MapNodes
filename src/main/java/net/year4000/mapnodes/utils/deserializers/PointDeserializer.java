package net.year4000.mapnodes.utils.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.year4000.mapnodes.game.regions.types.Point;

import java.lang.reflect.Type;

public class PointDeserializer implements JsonDeserializer<Point> {
    @Override
    public Point deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        String xyz = element.getAsJsonObject().get("xyz").getAsString();
        String[] xyzPoint = xyz.split(",");
        int x = element.getAsJsonObject().get("x").getAsInt();
        int y = element.getAsJsonObject().get("y").getAsInt();
        int z = element.getAsJsonObject().get("z").getAsInt();
        int yaw = element.getAsJsonObject().get("yaw").getAsInt();
        int pitch = element.getAsJsonObject().get("pitch").getAsInt();

        if (xyz.equals("")) {
            return new Point(x, y, z, yaw, pitch);
        }

        return new Point(Integer.valueOf(xyzPoint[0]), Integer.valueOf(xyzPoint[1]), Integer.valueOf(xyzPoint[2]), yaw, pitch);
    }
}
