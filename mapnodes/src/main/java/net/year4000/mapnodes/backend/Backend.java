/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.backend;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.Settings;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.game.NodePlayer;
import net.year4000.mapnodes.map.CoreMapObject;
import net.year4000.mapnodes.map.MapObject;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.FileMap;
import net.year4000.utilities.URLBuilder;
import net.year4000.utilities.sdk.API;
import net.year4000.utilities.sdk.HttpFetcher;
import net.year4000.utilities.sdk.routes.accounts.AccountRoute;
import org.bukkit.Bukkit;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Backend extends API {
    private static final String FILE_NAME = "/tmp/MapNodes/account_ids.json";
    public static Map<String, String> accounts = new FileMap<>(FILE_NAME);

    public Backend() {
        super(Settings.get().getKey());
        // Enforce valid UUIDs
        boolean bungeecord = Bukkit.spigot().getConfig().getBoolean("settings.bungeecord", false);
        if (!Bukkit.getOnlineMode() && !bungeecord) {
            Bukkit.spigot().getConfig().set("settings.bungeecord", true);
            throw new RuntimeException(Msg.util("server.offline"));
        }
    }

    /** Add tokens to the specific account */
    public void addTokens(GamePlayer player, int amount) {
        String accountUuid = player.getPlayer().getUniqueId().toString();
        String accountId = accounts.getOrDefault(accountUuid, accountUuid);
        URLBuilder url = api().addPath(ACCOUNTS_PATH).addPath(accountId).addPath("tokens");
        JsonObject data = new JsonObject();
        data.addProperty("amount", amount);
        HttpFetcher.put(url.build(), data, JsonObject.class, (response, error) -> {
            if (error == null) {
                accounts.putIfAbsent(accountUuid, response.get("id").getAsString());
            }
            else {
                MapNodesPlugin.log(new Exception(error), false);
            }
        });
    }

    /** Add tokens to the specific account */
    public void addExperience(GamePlayer player, int amount) {
        String accountUuid = player.getPlayer().getUniqueId().toString();
        String accountId = accounts.getOrDefault(accountUuid, accountUuid);
        URLBuilder url = api().addPath(ACCOUNTS_PATH).addPath(accountId).addPath("experience");
        JsonObject data = new JsonObject();
        data.addProperty("amount", amount);
        HttpFetcher.put(url.build(), data, JsonObject.class, (response, error) -> {
            if (error == null) {
                accounts.putIfAbsent(accountUuid, response.get("id").getAsString());
                NodePlayer nodePlayer = (NodePlayer) player;
                int xp = response.get("new_amount").getAsInt();
                nodePlayer.getCache().setExperience(xp);

                // If spectator update exp
                if (nodePlayer.isSpectator()) {
                    AccountRoute route = getAccount(accountUuid);
                    int level = route.getRawResponse().get("level").getAsInt();
                    int experience = route.getRawResponse().get("experience").getAsInt();
                    float experienceLevel = route.getRawResponse().get("experience_level").getAsFloat();
                    nodePlayer.getPlayer().setLevel(level);
                    nodePlayer.getPlayer().setTotalExperience(experience);
                    nodePlayer.getPlayer().setExp(experienceLevel);
                    nodePlayer.getCache().setNextExperienceLevel(experienceLevel);
                    nodePlayer.getCache().setLevel(level);
                }
            }
            else {
                MapNodesPlugin.log(new Exception(error), false);
            }
        });
    }

    /** Get a list of all possible maps */
    public List<MapObject> getMaps() {
        Type type = new TypeToken<List<MapObject>>() {}.getType();
        String url = api().addPath("maps").build();
        try {
            return HttpFetcher.get(url, type);
        }
        catch (Exception e) {
            MapNodesPlugin.log(Msg.util("maps.fetch.fail"));
            return new ArrayList<>();
        }
    }

    /** Get a map from the category and name */
    public CoreMapObject getMap(String category, String name) {
        String url = api().addPath("maps").addPath(category).addPath(name).build();
        try {
            return HttpFetcher.get(url, CoreMapObject.class);
        }
        catch (Exception e) {
            MapNodesPlugin.log(Msg.util("maps.fetch.fail"));
            return null;
        }
    }
}
