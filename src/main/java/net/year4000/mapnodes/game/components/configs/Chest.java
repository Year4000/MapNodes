package net.year4000.mapnodes.game.components.configs;

import com.google.gson.annotations.Since;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.year4000.mapnodes.api.game.configs.GameChest;

@Data
@NoArgsConstructor
public class Chest implements GameChest {
    /** The kit items */
    /*@Since(1.0)
    private List<ItemStack> items = new ArrayList<>();*/

    /** The max amount of randomness */
    @Since(1.0)
    private int amount = 10;

    /** Are the items scattered around the chest slots */
    @Since(1.0)
    private boolean scatter = true;

    /** Use list to fill the chests with */
    @Since(1.0)
    private boolean storage = false;
}
