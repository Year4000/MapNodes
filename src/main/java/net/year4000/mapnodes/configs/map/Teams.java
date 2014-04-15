package net.year4000.mapnodes.configs.map;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Setter(AccessLevel.PROTECTED)
@SuppressWarnings("all")
/** Mangaes the teams. */
public class Teams {
    /** The name of the team. */
    private String name = null;
    /** The color of the team. */
    private String color = null;
    /** The max size of the team. */
    private int size = 25;
    /** Are teammates save from each other. */
    private boolean allowFriendlyFire = false;
    /** Show each invisable teammates as ghosts. */
    private boolean canSeeFriendlyInvisibles = true;
    /** The kit this team will have. */
    private String kit = "";
    /** Points to where the player can spawn. */
    private Points[] spawns = null;
    /** Should this team be on the scoreboard. */
    private boolean useScoreboard = true;
    /** The game mode the player should be in. */
    private String gameMode = "survival";
    /** Can this team fly. */
    private boolean flying = false;
    /** The max health this team can have. */
    private double health = 20;
}
