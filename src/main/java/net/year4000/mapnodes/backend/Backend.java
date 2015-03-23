package net.year4000.mapnodes.backend;

import com.google.gson.JsonObject;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.Settings;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.game.NodePlayer;
import net.year4000.utilities.URLBuilder;
import net.year4000.utilities.sdk.API;
import net.year4000.utilities.sdk.HttpFetcher;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Backend extends API {
    private Map<UUID, String> accounts = new HashMap<>();

    public Backend() {
        super(Settings.get().getKey());
    }

    /** Add tokens to the specific account */
    public void addTokens(GamePlayer player, int amount) {
        UUID accountUuid = player.getPlayer().getUniqueId();
        URLBuilder url = api().addPath(ACCOUNTS_PATH).addPath(accountUuid.toString()).addPath("tokens");
        JsonObject data = new JsonObject();
        data.addProperty("amount", amount);

        HttpFetcher.put(url.build(), data, JsonObject.class, (response, error) -> {
            if (error == null) {
                accounts.putIfAbsent(accountUuid, response.get("id").getAsString());
            }
            else {
                MapNodesPlugin.debug(new Exception(error), false);
            }
        });
    }

    /** Add tokens to the specific account */
    public void addExperience(GamePlayer player, int amount) {
        UUID accountUuid = player.getPlayer().getUniqueId();
        URLBuilder url = api().addPath(ACCOUNTS_PATH).addPath(accountUuid.toString()).addPath("experience");
        JsonObject data = new JsonObject();
        data.addProperty("amount", amount);

        HttpFetcher.put(url.build(), data, JsonObject.class, (response, error) -> {
            if (error == null) {
                accounts.putIfAbsent(accountUuid, response.get("id").getAsString());
                NodePlayer nodePlayer = (NodePlayer) player;
                int xp = response.get("new_amount").getAsInt();
                int level = (int) Math.floor(Math.pow(xp, AccountCache.TWO_THIRDS) / 100);
                nodePlayer.getCache().setExperience(xp);
                nodePlayer.getCache().setLevel(level);
                nodePlayer.getCache().setNextExperienceLevel((int) (1000 * Math.pow(level + 1, AccountCache.TWO_THIRDS) * (level + 1)));
                nodePlayer.getCache().setLastExperienceLevel((int) (1000 * Math.pow(level == 0 ? 0 : level - 1, AccountCache.TWO_THIRDS) * (level == 0 ? 0 : level - 1)));
                nodePlayer.getCache().setCurrentExperienceLevel((int) (1000 * Math.pow(level, AccountCache.TWO_THIRDS) * (level)));
            }
            else {
                MapNodesPlugin.log(new Exception(error), false);
            }
        });
    }
}
