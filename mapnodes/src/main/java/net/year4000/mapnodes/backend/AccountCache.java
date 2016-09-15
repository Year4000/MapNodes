/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.backend;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import lombok.Data;
import net.year4000.mapnodes.game.NodePlayer;
import net.year4000.mapnodes.utils.FileMap;
import net.year4000.utilities.AbstractBadgeManager;

import java.util.Map;
import java.util.UUID;

@Data
public class AccountCache {
    private static final String FILE_NAME = "/tmp/MapNodes/accounts.json";
    private static final Map<String, AccountCache> cache = new FileMap<>(FILE_NAME);

    private long updated = System.currentTimeMillis();
    private UUID uuid;
    private String locale;
    private String rank;
    private AbstractBadgeManager.Badges badge;
    private int level;
    private int experience;
    private float nextExperienceLevel;

    private AccountCache(UUID uuid, JsonObject object) {
        this.uuid = uuid;
        this.rank = object.get("rank").getAsString();
        this.locale = object.get("locale").getAsString();
        this.badge = AbstractBadgeManager.Badges.valueOf(rank.toUpperCase());
        this.level = object.get("level").getAsInt();
        this.experience = object.get("experience").getAsInt();
        this.nextExperienceLevel = object.get("experience_level").getAsFloat();
        cache.put(uuid.toString(), this);
    }

    public static AccountCache getAccount(NodePlayer player) {
        return Preconditions.checkNotNull(cache.get(player.getPlayer().getUniqueId().toString()));
    }

    public static AccountCache getAccount(UUID uuid) {
        return Preconditions.checkNotNull(cache.get(uuid.toString()));
    }

    public static AccountCache createAccount(UUID uuid, JsonObject object) {
        return new AccountCache(uuid, object);
    }
}
