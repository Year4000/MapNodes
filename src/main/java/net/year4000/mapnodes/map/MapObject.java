/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.map;

import lombok.Data;

@Data
public class MapObject {
    private boolean disabled = false;
    private String category;
    private String name;
    private String version;
    private String description;
    private String[] authors;
    private String url;

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
}
