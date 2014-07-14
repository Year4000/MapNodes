package net.year4000.mapnodes.utils.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.LogUtil;
import org.bukkit.GameMode;

import java.lang.reflect.Type;

public class GameModeDeserializer implements JsonDeserializer<GameMode> {
    @Override
    public GameMode deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        String name = element.getAsString();

        try {
            return GameMode.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            LogUtil.debug(Msg.util("settings.gamemode"), name);
        }

        return GameMode.SURVIVAL;
    }
}
