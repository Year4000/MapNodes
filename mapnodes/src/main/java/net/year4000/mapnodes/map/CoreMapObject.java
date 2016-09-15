/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.map;

import com.google.gson.JsonObject;
import lombok.NonNull;
import lombok.experimental.NonFinal;

public class CoreMapObject {
    @NonFinal
    private MapObject object;
    private boolean disabled;
    @NonNull
    private JsonObject map;
    private ByteObject icon;
    private ByteObject world;

    @java.beans.ConstructorProperties({"object", "disabled", "map", "icon", "world"})
    public CoreMapObject(MapObject object, boolean disabled, JsonObject map, ByteObject icon, ByteObject world) {
        this.object = object;
        this.disabled = disabled;
        this.map = map;
        this.icon = icon;
        this.world = world;
    }

    /** Get the cache id */
    public String getCacheId() {
        return object.getURLName() + "-" + object.getVersion().replaceAll("\\.", "-");
    }

    public MapObject getObject() {
        return this.object;
    }

    public boolean isDisabled() {
        return this.disabled;
    }

    @NonNull
    public JsonObject getMap() {
        return this.map;
    }

    public ByteObject getIcon() {
        return this.icon;
    }

    public ByteObject getWorld() {
        return this.world;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof CoreMapObject)) return false;
        final CoreMapObject other = (CoreMapObject) o;
        final Object this$object = this.getObject();
        final Object other$object = other.getObject();
        if (this$object == null ? other$object != null : !this$object.equals(other$object)) return false;
        if (this.isDisabled() != other.isDisabled()) return false;
        final Object this$map = this.getMap();
        final Object other$map = other.getMap();
        if (this$map == null ? other$map != null : !this$map.equals(other$map)) return false;
        final Object this$icon = this.getIcon();
        final Object other$icon = other.getIcon();
        if (this$icon == null ? other$icon != null : !this$icon.equals(other$icon)) return false;
        final Object this$world = this.getWorld();
        final Object other$world = other.getWorld();
        if (this$world == null ? other$world != null : !this$world.equals(other$world)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $object = this.getObject();
        result = result * PRIME + ($object == null ? 43 : $object.hashCode());
        result = result * PRIME + (this.isDisabled() ? 79 : 97);
        final Object $map = this.getMap();
        result = result * PRIME + ($map == null ? 43 : $map.hashCode());
        final Object $icon = this.getIcon();
        result = result * PRIME + ($icon == null ? 43 : $icon.hashCode());
        final Object $world = this.getWorld();
        result = result * PRIME + ($world == null ? 43 : $world.hashCode());
        return result;
    }

    public String toString() {
        return "net.year4000.mapnodes.map.CoreMapObject(object=" + this.getObject() + ", disabled=" + this.isDisabled() + ", map=" + this.getMap() + ", icon=" + this.getIcon() + ", world=" + this.getWorld() + ")";
    }

    public void setObject(MapObject object) {
        this.object = object;
    }

    public static class ByteObject {
        private int size;
        private String url;

        @java.beans.ConstructorProperties({"size", "url"})
        public ByteObject(int size, String url) {
            this.size = size;
            this.url = url;
        }

        public int getSize() {
            return this.size;
        }

        public String getUrl() {
            return this.url;
        }

        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof ByteObject)) return false;
            final ByteObject other = (ByteObject) o;
            if (this.getSize() != other.getSize()) return false;
            final Object this$url = this.getUrl();
            final Object other$url = other.getUrl();
            if (this$url == null ? other$url != null : !this$url.equals(other$url)) return false;
            return true;
        }

        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            result = result * PRIME + this.getSize();
            final Object $url = this.getUrl();
            result = result * PRIME + ($url == null ? 43 : $url.hashCode());
            return result;
        }

        public String toString() {
            return "net.year4000.mapnodes.map.CoreMapObject.ByteObject(size=" + this.getSize() + ", url=" + this.getUrl() + ")";
        }
    }
}
