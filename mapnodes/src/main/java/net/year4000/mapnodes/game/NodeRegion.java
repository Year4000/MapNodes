/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.game;

import com.google.gson.annotations.Since;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.year4000.mapnodes.api.exceptions.InvalidJsonException;
import net.year4000.mapnodes.api.game.GameComponent;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.api.game.GameRegion;
import net.year4000.mapnodes.api.game.regions.PointVector;
import net.year4000.mapnodes.api.game.regions.Region;
import net.year4000.mapnodes.game.regions.RegionEvents;
import net.year4000.mapnodes.utils.typewrappers.RegionList;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

@Getter
@EqualsAndHashCode
public class NodeRegion implements GameRegion, GameComponent {
    /** The flags for this region optional if just used for zones */
    @Since(2.0)
    protected RegionEvents events = null;
    /** The zones that this region contains */
    @Since(2.0)
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    protected RegionList<Region> zones = new RegionList<>();

    @Override
    public void validate() throws InvalidJsonException {
        // Zones exist and their is at least one location point
        checkArgument(zones != null && zones.size() > 0);
    }

    /*//--------------------------------------------//
         Upper Json Settings / Bellow Instance Code
    *///--------------------------------------------//

    private static transient int totalWeight = 0;
    @Getter(lazy = true)
    private final transient String id = id();
    private transient int weight = 0;
    private transient GameManager game;
    private transient Set<Region> zoneSet = new HashSet<>();

    /** Region weight */
    public NodeRegion() {
        addEvent();
    }

    /** Assign the game to this region */
    public void assignNodeGame(GameManager game) {
        this.game = game;
    }

    /** Get the id of this class and cache it */
    private String id() {
        NodeRegion thisObject = this;

        for (Map.Entry<String, GameRegion> entry : game.getRegions().entrySet()) {
            if (thisObject.equals(entry.getValue())) {
                return entry.getKey();
            }
        }

        throw new RuntimeException("Can not find the id of " + this.toString());
    }

    /** Make this event tracked by the event system and give it weight */
    public void addEvent() {
        totalWeight += 10;
        weight = totalWeight;
    }

    /** Get the zone set with zone caching from conversion */
    public Set<Region> getZones() {
        if (zoneSet.size() == 0) {
            zoneSet.addAll(zones);
        }

        return zoneSet;
    }

    /** Is the point in the region */
    public boolean inZone(PointVector point) {
        return getZones().stream().filter(z -> z.inRegion(point)).count() > 0;
    }
}
