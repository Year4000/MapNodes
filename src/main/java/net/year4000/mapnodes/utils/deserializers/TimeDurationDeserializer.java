package net.year4000.mapnodes.utils.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.year4000.mapnodes.utils.TimeDuration;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class TimeDurationDeserializer implements JsonDeserializer<TimeDuration> {
    @Override
    public TimeDuration deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        String value = element.getAsJsonPrimitive().isString() ? element.getAsString() : String.valueOf(element.getAsInt());
        boolean forever = value.equals("-1");

        value = value.contains("m") || value.contains("s") || value.contains("h") ? value : value + "m";

        return new TimeDuration(TimeUnit.MILLISECONDS, forever ? Integer.MAX_VALUE : Duration.parse("PT" + value.toUpperCase()).toMillis(), forever);
    }
}
