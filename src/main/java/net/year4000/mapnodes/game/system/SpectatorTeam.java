package net.year4000.mapnodes.game.system;

import net.year4000.mapnodes.game.NodeTeam;
import net.year4000.mapnodes.utils.typewrappers.LocationList;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class SpectatorTeam extends NodeTeam implements Spectator {
    public SpectatorTeam(LocationList<Location> spawns) {
        setName("Spectator");

        setColor(ChatColor.GRAY);

        setKit("spectator");

        setSize(Integer.MAX_VALUE);

        setSpawns(spawns);

        setUseScoreboard(false);
    }
}
