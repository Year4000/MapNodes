package net.year4000.mapnodes.utils.deserializers;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeConfig;
import net.year4000.mapnodes.game.NodeModeFactory;
import net.year4000.mapnodes.utils.GsonUtil;
import net.year4000.mapnodes.utils.typewrappers.GameSet;

import java.lang.reflect.Type;
import java.util.Map;

public class GameModesDeserializer implements JsonDeserializer<GameSet<GameMode>> {
    @Override
    public GameSet<GameMode> deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        GameSet<GameMode> newSet = new GameSet<>();

        // element is hash map
        Map<String, JsonObject> modes = GsonUtil.createGson().fromJson(element, new TypeToken<Map<String, JsonObject>>() {
        }.getType());

        // loop through each element and create a new instance of the game mode
        modes.forEach((name, config) -> {
            // Create config class from registered name
            GameModeConfig gameModeConfig = GsonUtil.createGson().fromJson(config, NodeModeFactory.get().getGameModeConfig(name));
            GameMode gameMode = NodeModeFactory.get().getFromConfig(gameModeConfig);

            newSet.add(gameMode);
        });

        return newSet;
    }
}
