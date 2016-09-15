/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.map;

public class MapObject {
    private boolean disabled = false;
    private String category;
    private String name;
    private String version;
    private String description;
    private String[] authors;
    private String url;

    public MapObject() {
    }

    /** Get the url safe version of the category */
    public String getURLCategory() {
        return category.toLowerCase().replaceAll(" ", "-");
    }

    /** Get the url safe version of the map name */
    public String getURLName() {
        return name.toLowerCase().replaceAll(" ", "-");
    }

    @Override
    public String toString() {
        return category + "/" + name + " (" + (disabled ? "Disabled" : "Enabled") + ")";
    }

    public boolean isDisabled() {
        return this.disabled;
    }

    public String getCategory() {
        return this.category;
    }

    public String getName() {
        return this.name;
    }

    public String getVersion() {
        return this.version;
    }

    public String getDescription() {
        return this.description;
    }

    public String[] getAuthors() {
        return this.authors;
    }

    public String getUrl() {
        return this.url;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAuthors(String[] authors) {
        this.authors = authors;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof MapObject)) return false;
        final MapObject other = (MapObject) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.isDisabled() != other.isDisabled()) return false;
        final Object this$category = this.getCategory();
        final Object other$category = other.getCategory();
        if (this$category == null ? other$category != null : !this$category.equals(other$category)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final Object this$version = this.getVersion();
        final Object other$version = other.getVersion();
        if (this$version == null ? other$version != null : !this$version.equals(other$version)) return false;
        final Object this$description = this.getDescription();
        final Object other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description))
            return false;
        if (!java.util.Arrays.deepEquals(this.getAuthors(), other.getAuthors())) return false;
        final Object this$url = this.getUrl();
        final Object other$url = other.getUrl();
        if (this$url == null ? other$url != null : !this$url.equals(other$url)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + (this.isDisabled() ? 79 : 97);
        final Object $category = this.getCategory();
        result = result * PRIME + ($category == null ? 43 : $category.hashCode());
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $version = this.getVersion();
        result = result * PRIME + ($version == null ? 43 : $version.hashCode());
        final Object $description = this.getDescription();
        result = result * PRIME + ($description == null ? 43 : $description.hashCode());
        result = result * PRIME + java.util.Arrays.deepHashCode(this.getAuthors());
        final Object $url = this.getUrl();
        result = result * PRIME + ($url == null ? 43 : $url.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof MapObject;
    }
}
