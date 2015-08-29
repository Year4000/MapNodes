/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import net.year4000.utilities.URLBuilder;
import net.year4000.utilities.sdk.API;
import net.year4000.utilities.sdk.HttpFetcher;

import java.util.Arrays;
import java.util.List;

@Getter
@ToString
public class Settings {
    private static transient Settings inst = null;

    @NonNull
    @SerializedName("client_locales")
    private String clientLocales = "https://raw.githubusercontent.com/Year4000/Locales/master/net/year4000/mapnodes/locales/";

    @NonNull
    @SerializedName("system_locales")
    private String systemLocales = "https://raw.githubusercontent.com/Year4000/Locales/master/net/year4000/mapnodes/system/";

    @NonNull
    @SerializedName("load_maps")
    private Integer loadMaps = 0;

    @NonNull
    @SerializedName("world_cache")
    private String worldCache = "/tmp/MapNodes";

    @NonNull
    private JsonObject kits = new JsonObject();

    // todo remove this, was added for backwards compatibly
    private transient List<String> modes = Arrays.asList(System.getProperty("modes", ",").toLowerCase().split("(,|;)"));

    // todo remove this, was added for backwards compatibly
    public transient String key = System.getenv("Y4K_KEY");

    public static Settings get() {
        if (inst == null) {
            try {
                URLBuilder url = URLBuilder.builder(API.BASE_URL).addPath("configs").addPath("mapnodes");
                inst = HttpFetcher.get(url.build(), Settings.class);
                overrideSettings(inst);
            }
            catch (Exception e) {
                MapNodesPlugin.log(e, false);
                inst = new Settings();
                overrideSettings(inst);
            }
        }

        return inst;
    }

    /** JVM properties to override settings */
    private static void overrideSettings(Settings settings) {
        String loadMaps = "mapnodes.load_maps";
        if ((loadMaps = System.getProperty(loadMaps)) != null) {
            settings.loadMaps = Integer.valueOf(loadMaps);
        }

        String worldCache = "mapnodes.world_cache";
        if ((worldCache = System.getProperty(worldCache)) != null) {
            settings.worldCache = worldCache;
        }
    }
}
