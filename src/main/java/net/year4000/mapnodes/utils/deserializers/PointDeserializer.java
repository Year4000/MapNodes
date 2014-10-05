package net.year4000.mapnodes.utils.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.year4000.mapnodes.game.regions.types.Point;
import net.year4000.mapnodes.utils.GsonUtil;

import java.lang.reflect.Type;

public class PointDeserializer implements JsonDeserializer<Point> {
    @Override
    public Point deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        SimplePoint simplePoint = GsonUtil.GSON.fromJson(element, SimplePoint.class);
        String xyz = simplePoint.xyz;
        String[] xyzPoint = xyz.split(",");

        if (xyz == null || xyz.equals("")) {
            return new Point(simplePoint.x, simplePoint.y, simplePoint.z, simplePoint.yaw, simplePoint.pitch);
        }

        return new Point(getPoint(xyzPoint[0]), getPoint(xyzPoint[1]), getPoint(xyzPoint[2]), simplePoint.yaw, simplePoint.pitch);
    }

    public int getPoint(String point) {
        return Integer.valueOf(point.replaceAll(" ", ""));
    }

    public class SimplePoint {
        public String xyz = null;
        public int x = 0;
        public int y = 0;
        public int z = 0;
        public Integer yaw = null;
        public Integer pitch = null;
    }
}
