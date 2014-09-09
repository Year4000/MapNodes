package net.year4000.mapnodes.game.components.regions.types;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.exceptions.InvalidJsonException;
import net.year4000.mapnodes.game.components.regions.Region;
import net.year4000.mapnodes.game.components.regions.RegionType;
import net.year4000.mapnodes.utils.Validator;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

@Data
@NoArgsConstructor
@RegionType("sphere")
public class Sphere implements Region, Validator {
    private Point center = null;
    private Integer radius = null;
    private Integer yaw;
    private Integer pitch;

    @Override
    public List<Location> getLocations(World world) {
        return getPoints().stream().map(p -> p.create(world)).collect(Collectors.toList());
    }

    @Override
    public List<Point> getPoints() {
        List<Point> locations = new ArrayList<>();
        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();

        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int z = cz - radius; z <= cz + radius; z++) {
                for (int y = cy - radius; y < cy + radius; y++) {
                    locations.add(new Point(x, y, z, yaw, pitch));
                }
            }
        }

        return locations;
    }

    @Override
    public boolean inRegion(Point region) {
        return region.getLocations(MapNodes.getCurrentWorld()).get(0).distance(center.getLocations(MapNodes.getCurrentWorld()).get(0)) <= radius;
    }

    @Override
    public void validate() throws InvalidJsonException {
        checkArgument(center != null);
        center.validate();
        checkArgument(radius != null && radius != 0);
    }
}
