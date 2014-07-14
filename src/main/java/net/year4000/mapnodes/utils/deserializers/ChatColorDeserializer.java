package net.year4000.mapnodes.utils.deserializers;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.LogUtil;
import org.bukkit.ChatColor;

import java.lang.reflect.Type;

public class ChatColorDeserializer  implements JsonDeserializer<ChatColor> {
    @Override
    public ChatColor deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        String name = element.getAsString();

        try {
            return ChatColor.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            LogUtil.debug(Msg.util("settings.color"), name);
        }

        return ChatColor.WHITE;
    }
}
