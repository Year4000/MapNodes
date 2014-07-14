package net.year4000.mapnodes.game.components.regions;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@NoArgsConstructor
public class Point implements Region, Validator {
    private Integer x = null;
    private Integer y = null;
    private Integer z = null;

    @Override
    public void validate() throws InvalidJsonException {
        checkArgument(x != null, Msg.util("settings.region"), "X");

        checkArgument(y != null, Msg.util("settings.region"), "Y");

        checkArgument(z != null, Msg.util("settings.region"), "Z");
    }

    @Override
    public List<Location> getLocations(World world) {
        List<Location> locations = new ArrayList<>();

        locations.add(create(world));

        return locations;
    }

    public Location create(World world) {
        return new Location(world, x, y, x);
    }
}
