/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.game;

import com.google.gson.annotations.Since;
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

public class NodeRegion implements GameRegion, GameComponent {
    /** The flags for this region optional if just used for zones */
    @Since(2.0)
    protected RegionEvents events = null;
    /** The zones that this region contains */
    @Since(2.0)
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

    public RegionEvents getEvents() {
        return this.events;
    }

    public int getWeight() {
        return this.weight;
    }

    public GameManager getGame() {
        return this.game;
    }

    public Set<Region> getZoneSet() {
        return this.zoneSet;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof NodeRegion)) return false;
        final NodeRegion other = (NodeRegion) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$events = this.getEvents();
        final Object other$events = other.getEvents();
        if (this$events == null ? other$events != null : !this$events.equals(other$events)) return false;
        final Object this$zones = this.getZones();
        final Object other$zones = other.getZones();
        if (this$zones == null ? other$zones != null : !this$zones.equals(other$zones)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $events = this.getEvents();
        result = result * PRIME + ($events == null ? 43 : $events.hashCode());
        final Object $zones = this.getZones();
        result = result * PRIME + ($zones == null ? 43 : $zones.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof NodeRegion;
    }

    public String getId() {
        return this.id;
    }
}
