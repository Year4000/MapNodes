package net.year4000.mapnodes.game.regions.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.year4000.mapnodes.exceptions.InvalidJsonException;
import net.year4000.mapnodes.game.regions.Region;
import net.year4000.mapnodes.game.regions.RegionType;
import net.year4000.mapnodes.game.regions.RegionTypes;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Validator;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BlockVector;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RegionType(RegionTypes.POINT)
public class Point implements Region, Validator {
    private Integer x = null;
    private Integer y = null;
    private Integer z = null;
    private transient Integer yaw;
    private transient Integer pitch;

    public Point(Integer x, Integer y, Integer z) {
        this(x, y, z, null, null);
    }

    public Point(BlockVector vector) {
        this(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ(), null, null);
    }

    @Override
    public void validate() throws InvalidJsonException {
        checkArgument(x != null, Msg.util("settings.region", "X"));

        checkArgument(y != null, Msg.util("settings.region", "Y"));

        checkArgument(z != null, Msg.util("settings.region", "Z"));
    }

    @Override
    public List<Location> getLocations(World world) {
        List<Location> locations = new ArrayList<>();

        locations.add(create(world));

        return locations;
    }

    @Override
    public List<Point> getPoints() {
        List<Point> locations = new ArrayList<>();

        locations.add(this);

        return locations;
    }

    @Override
    public boolean inRegion(Point region) {
        return region.getX().equals(x < 0 ? x - 1 : x) && region.getY().equals(y) && region.getZ().equals(z < 0 ? z - 1 : z);
    }

    public Location create(World world) {
        if (yaw == null || pitch == null) {
            return new Location(world, x < 0 ? x - 1 : x, y, z < 0 ? z - 1 : z);
        }

        return new Location(world, x < 0 ? x - 1 : x, y, z < 0 ? z - 1 : z, yaw, pitch);
    }
}
