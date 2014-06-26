package net.year4000.mapnodes.configs.map;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Setter(AccessLevel.PROTECTED)
@SuppressWarnings("all")
/** Details about the current map. */
public class Map {
    /** The name of the current map. */
    private String name = null;
    /** The version of the current map. */
    private String version = null;
    /** The object/goal of the current map. */
    private String description = null;
    /** Any one that has helped with current the map. */
    private String[] authors = null;
}