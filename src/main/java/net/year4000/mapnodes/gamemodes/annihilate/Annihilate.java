package net.year4000.mapnodes.gamemodes.annihilate;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.gamemodes.GameModeTemplate;

@GameModeInfo(
    name = "Annihilate",
    version = "1.0",
    config = AnnihilateConfig.class,
    listeners = {AnnihilateListener.class}
)
@Data
@EqualsAndHashCode(callSuper = true)
public class Annihilate extends GameModeTemplate implements GameMode {
}
