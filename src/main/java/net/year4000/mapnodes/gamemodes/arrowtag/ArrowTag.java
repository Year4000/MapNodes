package net.year4000.mapnodes.gamemodes.arrowtag;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.gamemodes.GameModeTemplate;

@GameModeInfo(
    name = "Arrow Tag",
    version = "1.0",
    config = ArrowTagConfig.class,
    listeners = {ArrowTagListener.class}
)
@Data
@EqualsAndHashCode(callSuper = true)
public class ArrowTag extends GameModeTemplate implements GameMode {
}
