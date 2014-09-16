package net.year4000.mapnodes.game.regions;

import net.year4000.mapnodes.game.regions.types.Point;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;

public interface Region {
    /** Get a list of locations that is region has used for player tp */
    public List<Location> getLocations(World world);

    /** Get a list of all vector points */
    public List<Point> getPoints();

    /** Is the point in the region */
    public boolean inRegion(Point region);
}
