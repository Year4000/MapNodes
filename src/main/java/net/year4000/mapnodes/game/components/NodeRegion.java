package net.year4000.mapnodes.game.components;

import com.google.gson.annotations.Since;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.year4000.mapnodes.api.game.GameRegion;
import net.year4000.mapnodes.exceptions.InvalidJsonException;
import net.year4000.mapnodes.game.components.regions.Region;
import net.year4000.mapnodes.game.components.regions.types.Point;
import net.year4000.mapnodes.game.components.regions.RegionFlags;
import net.year4000.mapnodes.utils.Validator;
import net.year4000.mapnodes.utils.typewrappers.LocationList;
import net.year4000.mapnodes.utils.typewrappers.RegionList;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

@Data
public final class NodeRegion implements GameRegion, Validator, Comparable {
    /** The teams this region apply to option if just used for zones */
    @Since(1.0)
    private List<String> apply = new ArrayList<>();

    /** The weight of the region */
    @Since(1.0)
    private Integer weight = null;

    /** The flags for this region optional if just used for zones */
    @Since(1.0)
    private RegionFlags flags = null;

    /** The zones that this region contains */
    @Since(1.0)
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private RegionList<Region> zones = new RegionList<>();

    @Override
    public void validate() throws InvalidJsonException {
        // Zones exist and their is at least one location point
        checkArgument(zones != null && zones.size() > 0);

        // Validate flags if we have flags and make sure we have a weight
        if (flags != null) {
            flags.validate(this);

            checkArgument(weight != null);
        }
    }

    /*//--------------------------------------------//
         Upper Json Settings / Bellow Instance Code
    *///--------------------------------------------//

    private Set<Region> zoneSet = new HashSet<>();

    /** Get the zone set with zone caching from conversion */
    public Set<Region> getZones() {
        if (zoneSet.size() == 0) {
            zoneSet.addAll(zones);
        }

        return zoneSet;
    }

    public boolean inZone(Point point) {
        return getZones().stream().filter(z -> z.getPoints().contains(point)).count() > 0;
    }

    // todo should event be stopped

    /** Compare the node regions so that we can handle zone weights */
    @Override
    public int compareTo(Object o) {
        if (!(o instanceof NodeRegion)) {
            return -1;
        }
        else if (((NodeRegion) o).getWeight() > weight) {
            return 1;
        }
        else if (((NodeRegion) o).getWeight() < weight) {
            return -1;
        }
        else {
            return 0;
        }
    }
}
