package net.year4000.mapnodes.configs;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.year4000.mapnodes.configs.map.*;

import java.util.HashMap;

@Data
@NoArgsConstructor
@SuppressWarnings("unused")
public class MapConfig {
    /** Details about the current map. */
    private Map map = null;
    /** General game settings. */
    private Game game;
    /** Manage the items and effects that are given to the player. */
    private HashMap<String, Kits> kits;
    /** Manages the teams. */
    private HashMap<String, Teams> teams;
    /** Classes to pick from on top of your team. */
    private HashMap<String, Classes> classes = null;
}
