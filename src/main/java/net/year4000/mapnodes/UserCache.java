package net.year4000.mapnodes;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Data;
import net.year4000.mapnodes.messages.Git;
import net.year4000.mapnodes.utils.GsonUtil;

import java.io.*;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class UserCache {
    private final static String GIT_HASH = Git.get("git.commit.id");
    private final Gson GSON = GsonUtil.gsonBuilder().setPrettyPrinting().create();
    private final File STORAGE = new File(MapNodesPlugin.getInst().getDataFolder(), "user-cache.json");
    private Storage db = new Storage();

    public UserCache() {
        if (!MapNodesPlugin.getInst().getDataFolder().exists()) {
            MapNodesPlugin.getInst().getDataFolder().mkdir();
        }

        if (!STORAGE.exists()) {
            write(STORAGE);
        }

        read(STORAGE);
    }

    public void write(File storage) {
        try (FileWriter file = new FileWriter(storage)) {
            file.write(GSON.toJson(db));
        } catch (IOException e) {
            MapNodesPlugin.log(e, false);
        }
    }

    public void read(File storage) {
        try {
            JsonObject db = GSON.fromJson(new FileReader(storage), JsonObject.class);
            boolean sameHash = db.get("hash").getAsString().equals(GIT_HASH);
            boolean expired = System.currentTimeMillis() > db.get("expires").getAsLong();

            if (!sameHash || expired) {
                write(storage);
                read(storage);
            }

            this.db = GSON.fromJson(new FileReader(storage), Storage.class);
        } catch (FileNotFoundException e) {
            MapNodesPlugin.log(e, false);
        }
    }

    public String getPlayer(UUID uuid) {
        return db.getUsers().getOrDefault(uuid.toString(), "unknown");
    }

    // replace the record
    public void addPlayer(UUID uuid, String record) {
        db.getUsers().put(uuid.toString(), record);

        write(STORAGE);
    }

    // replace the record

    public boolean hasUUID(UUID name) {
        return db.getUsers().containsKey(name.toString());
    }

    @Data
    private class Storage {
        private String hash = GIT_HASH;
        private Long expires = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1);
        private HashMap<String, String> users = new HashMap<>();
    }
}
