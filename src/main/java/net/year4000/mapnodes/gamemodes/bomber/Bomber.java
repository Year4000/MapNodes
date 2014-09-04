package net.year4000.mapnodes.gamemodes.bomber;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.gamemodes.GameModeTemplate;

@GameModeInfo(
    name = "Bommer",
    version = "1.0",
    config = BomberConfig.class,
    listeners = {BomberListener.class}
)
@Data
@EqualsAndHashCode(callSuper = true)
public class Bomber extends GameModeTemplate implements GameMode {
}
