package net.year4000.mapnodes.gamemodes.spleef;

import com.google.common.collect.ImmutableList;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.api.game.modes.GameModeConfig;
import net.year4000.mapnodes.api.game.modes.GameModeConfigName;
import net.year4000.mapnodes.gamemodes.elimination.EliminationConfig;
import net.year4000.mapnodes.utils.typewrappers.MaterialList;
import org.bukkit.Material;

@Data
@EqualsAndHashCode(callSuper = false)
@GameModeConfigName("spleef_runner")
public class SpleefRunnerConfig extends EliminationConfig implements GameModeConfig {
    private transient ImmutableList<Material> defaultBlockTypes = ImmutableList.<Material>builder()
        .add(Material.CLAY)
        .add(Material.WOOL)
        .add(Material.HARD_CLAY)
        .add(Material.STAINED_GLASS)
        .add(Material.STAINED_GLASS_PANE)
        .add(Material.STAINED_CLAY)
        .add(Material.STONE)
        .add(Material.SMOOTH_BRICK)
        .add(Material.QUARTZ_BLOCK)
        .build();

    private MaterialList<Material> blocks = new MaterialList<>(defaultBlockTypes);
}
