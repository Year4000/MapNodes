package net.year4000.mapnodes.game;

import com.google.gson.annotations.Since;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.api.game.GameRegion;
import net.year4000.mapnodes.exceptions.InvalidJsonException;
import net.year4000.mapnodes.game.regions.Region;
import net.year4000.mapnodes.game.regions.types.Point;
import net.year4000.mapnodes.game.regions.RegionFlags;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.GameValidator;
import net.year4000.mapnodes.utils.typewrappers.RegionList;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

@Data
public final class NodeRegion implements GameRegion, GameValidator, Comparable {
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

    @Override
    public void validate(GameManager game) throws InvalidJsonException {
        this.game = game;
        validate();
    }

    /*//--------------------------------------------//
         Upper Json Settings / Bellow Instance Code
    *///--------------------------------------------//

    @Setter(AccessLevel.NONE)
    private transient String id;
    private transient GameManager game;
    private Set<Region> zoneSet = new HashSet<>();

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

    /** Get a list of all regions sorted by their weight */
    public static List<GameRegion> getRegions(Point point) {
        return ((NodeGame) MapNodes.getCurrentGame()).getRegions().values().stream()
            .filter(r -> r.inZone(point))
            .sorted()
            .collect(Collectors.toList());
    }

    /** Send the deny message or default message */
    public static void sendDenyMessage(Player player, NodeRegion region, String key) {
        if (region.getFlags().getMessage() != null) {
            player.sendMessage(MapNodes.getCurrentGame().locale(player.getLocale(), region.getFlags().getMessage()));
        }
        else {
            player.sendMessage(Msg.locale(player, key));
        }
    }

    /** Does the region apply to the current player */
    public static boolean applyToPlayer(Player player, NodeRegion region) {
        String team = MapNodes.getCurrentGame().getPlayer(player).getTeam().getName().toLowerCase();
        return region.getApply().stream().filter(apply -> apply.toLowerCase().equals(team)).count() > 0L;
    }

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
