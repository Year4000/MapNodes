package net.year4000.mapnodes.map;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.year4000.mapnodes.map.components.*;
import java.util.HashMap;

@Data
@NoArgsConstructor
public class MapSettings {
    /** Details about the current map. */
    private Map map = null;
    /** General game settings. */
    private Game game;
    /** Manage the items and effects that are given to the player. */
    private java.util.Map<String, Kits> kits = new HashMap<>();
    /** Manages the teams. */
    private java.util.Map<String, Teams> teams = new HashMap<>();
    /** Classes to pick from on top of your team. */
    private java.util.Map<String, Classes> classes = new HashMap<>();
}
