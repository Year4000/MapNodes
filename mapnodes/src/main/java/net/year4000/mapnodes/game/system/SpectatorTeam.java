/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.game.system;

import net.year4000.mapnodes.api.utils.Spectator;
import net.year4000.mapnodes.game.NodeTeam;
import net.year4000.mapnodes.utils.typewrappers.LocationList;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class SpectatorTeam extends NodeTeam implements Spectator {
    public SpectatorTeam(LocationList<Location> spawns) {
        name = "Spectator";

        color = ChatColor.GRAY;

        kit = NodeTeam.SPECTATOR;

        size = Integer.MAX_VALUE;

        this.spawns = spawns;

        useScoreboard = false;
    }
}
