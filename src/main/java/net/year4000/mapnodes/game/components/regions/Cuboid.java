package net.year4000.mapnodes.game.components.regions;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.year4000.mapnodes.exceptions.InvalidJsonException;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Validator;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

@Data
@NoArgsConstructor
public class Cuboid implements Region, Validator {
    private Point min = null;
    private Point max = null;

    @Override
    public void validate() throws InvalidJsonException {
        checkArgument(min != null, Msg.util("settings.region", "min"));

        min.validate();

        checkArgument(max != null, Msg.util("settings.region", "max"));

        max.validate();
    }

    @Override
    public List<Location> getLocations(World world) {
        List<Location> locations = new ArrayList<>();

        for (int y = min.getY(); y <= max.getY(); y++) {
            for (int x = min.getX(); x <= max.getX(); x++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    locations.add(new Point(x, y, z).create(world));
                }
            }
        }

        return locations;
    }

}
