/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.map;

import com.google.gson.JsonObject;
import lombok.NonNull;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
public class CoreMapObject {
    @NonFinal
    @Setter
    private MapObject object;
    private boolean disabled;
    @NonNull
    private JsonObject map;
    private ByteObject icon;
    private ByteObject world;

    /** Get the cache id */
    public String getCacheId() {
        return object.getURLName() + "-" + object.getVersion().replaceAll("\\.", "-");
    }

    @Value
    public static class ByteObject {
        private int size;
        private String url;
    }
}
