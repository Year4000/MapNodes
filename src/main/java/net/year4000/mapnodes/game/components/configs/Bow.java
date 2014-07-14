package net.year4000.mapnodes.game.components.configs;

import com.google.gson.annotations.Since;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.year4000.mapnodes.api.game.configs.GameBow;
import org.bukkit.entity.EntityType;

@Data
@NoArgsConstructor
public class Bow implements GameBow {
    /** The entity that gets shot from the bow. */
    @Since(1.0)
    private EntityType entity = EntityType.ARROW;

    /** The velocity that the entity is thrown at. */
    @Since(1.0)
    private double velocity = 1;
}
