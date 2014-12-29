package net.year4000.mapnodes.game.kits;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.year4000.mapnodes.api.exceptions.InvalidJsonException;
import net.year4000.mapnodes.api.utils.Validator;
import org.bukkit.Material;

@Data
@NoArgsConstructor
public class Armor implements Validator {
    /** The slot number to appear in the inventory. */
    private Item helmet = air();
    private Item chestplate = air();
    private Item leggings = air();
    private Item boots = air();

    /** Create a blank air item */
    private Item air() {
        Item air = new Item();

        air.setItem(Material.AIR);
        air.setAmount(1);

        return air;
    }

    @Override
    public void validate() throws InvalidJsonException {
        helmet.validate();
        chestplate.validate();
        leggings.validate();
        boots.validate();
    }
}
