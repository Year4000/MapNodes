package net.year4000.mapnodes.game;

import com.google.gson.annotations.Since;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.GameRegion;
import net.year4000.mapnodes.exceptions.InvalidJsonException;
import net.year4000.mapnodes.game.regions.Region;
import net.year4000.mapnodes.game.regions.RegionEvents;
import net.year4000.mapnodes.game.regions.types.Point;
import net.year4000.mapnodes.utils.AssignNodeGame;
import net.year4000.mapnodes.utils.Validator;
import net.year4000.mapnodes.utils.typewrappers.RegionList;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

@Data
public final class NodeRegion implements GameRegion, Validator, AssignNodeGame {
    /** The flags for this region optional if just used for zones */
    @Since(1.0)
    private RegionEvents events = null;

    /** The zones that this region contains */
    @Since(1.0)
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private RegionList<Region> zones = new RegionList<>();

    @Override
    public void validate() throws InvalidJsonException {
        // Zones exist and their is at least one location point
        checkArgument(zones != null && zones.size() > 0);
    }

    /*//--------------------------------------------//
         Upper Json Settings / Bellow Instance Code
    *///--------------------------------------------//

    private transient NodeGame game;
    @Setter(AccessLevel.NONE)
    private transient String id;
    private transient Set<Region> zoneSet = new HashSet<>();

    /** Assign the game to this region */
    public void assignNodeGame(NodeGame game) {
        this.game = game;
    }

    /** Get the id of this class and cache it */
    public String getId() {
        if (id == null) {
            NodeRegion thisObject = this;

            game.getRegions().forEach((string, object) -> {
                if (object.equals(thisObject)) {
                    id = string;
                }
            });
        }

        return id;
    }

    /** Get the zone set with zone caching from conversion */
    public Set<Region> getZones() {
        if (zoneSet.size() == 0) {
            zoneSet.addAll(zones);
        }

        return zoneSet;
    }

    /** Is the point in the region */
    public boolean inZone(Point point) {
        return getZones().stream().filter(z -> z.inRegion(point)).count() > 0;
    }
}
