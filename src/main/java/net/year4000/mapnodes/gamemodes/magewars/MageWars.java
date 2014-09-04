package net.year4000.mapnodes.gamemodes.magewars;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.gamemodes.GameModeTemplate;

@GameModeInfo(
    name = "MageWars",
    version = "1.0",
    config = MageWarsConfig.class,
    listeners = {MageWarsListener.class}
)
@Data
@EqualsAndHashCode(callSuper = true)
public class MageWars extends GameModeTemplate implements GameMode {
}
