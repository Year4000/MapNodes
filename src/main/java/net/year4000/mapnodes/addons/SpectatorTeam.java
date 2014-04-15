package net.year4000.mapnodes.addons;

import net.year4000.mapnodes.configs.map.Points;
import net.year4000.mapnodes.configs.map.Teams;
import net.year4000.mapnodes.game.GameManager;
import org.bukkit.Location;

public class SpectatorTeam extends Teams {
    public SpectatorTeam(GameManager gm) {
        setAllowFriendlyFire(false);
        setCanSeeFriendlyInvisibles(true);
        setColor("gray");
        setName("SPECTATOR");
        setSize(-1);
        final Location spawnLoc = gm.getMap().getSpawn().get(0);
        Points[] spawn = new Points[1];
        spawn[0] = new Points() {{
            setPoint(new Cordient() {{
                setX((int)spawnLoc.getX());
                setY((int)spawnLoc.getY());
                setZ((int)spawnLoc.getZ());
            }});
        }};
        setSpawns(spawn);
        setUseScoreboard(false);
        setKit("SPECTATOR");
    }
}
