package net.year4000.mapnodes.game.components.regions;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;

public interface Region {
    public List<Location> getLocations(World world);
}
