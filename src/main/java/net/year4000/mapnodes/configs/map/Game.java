package net.year4000.mapnodes.configs.map;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Setter(AccessLevel.PROTECTED)
@SuppressWarnings("all")
/** General game settings. */
public class Game {
    /** The time limit of the map. */
    @Deprecated
    private int timeLimit = 10;
    /** The map's difficulty level. */
    private int difficulty = 3;
    /** The time that the map should be locked to. */
    private int timeLock = -1;
    /** Should the weather be forced on. */
    private boolean forceWeather = false;
    /** Can the map be destoyed. */
    private boolean destructable = true;
    /** The height of the world. */
    private int worldHeight = -1;
    /** What damage should be ignore from the player. */
    private String[] noDamage = new String[] {};
    /** What mobs should be allowed in the map. */
    private String[] enabledMobs = new String[] {};
    /** What items should be droped from the player. */
    private String[] enabledPlayerDrops = new String[] {};
    /** All settings for tnt. */
    private Tnt tnts = new Tnt();
    /** All settings for bows. */
    private Bow bows = new Bow();
    /** The area for the spawn. */
    private Points spawn = new Points();


    @Data
    @NoArgsConstructor
    /** All settings for bows. */
    public class Bow {
        /** The entity that gets shot from the bow. */
        private String entity = "ARROW";
        /** The velocity that the entity is thrown at. */
        private double velocity;
    }

    @Data
    @NoArgsConstructor
    /** All settings for tnt. */
    public class Tnt {
        /** Should tnt be activated when its placed. */
        private boolean instant = false;
        /** Should the tnt destroy blocks. */
        private boolean blockDamage = true;
        /** The percent of blocks should appear. */
        private int drops = 100;
    }
}