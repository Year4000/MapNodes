package net.year4000.mapnodes.api.game;

import net.year4000.mapnodes.api.game.regions.PointVector;
import net.year4000.mapnodes.api.game.regions.Region;

import java.util.Set;

public interface GameRegion extends GameComponent {
    public Set<Region> getZones();

    public boolean inZone(PointVector vector);

    public int getWeight();

    public Set<Region> getZoneSet();
}
