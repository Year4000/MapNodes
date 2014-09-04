package net.year4000.mapnodes.gamemodes.endtimes;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.gamemodes.GameModeTemplate;

@GameModeInfo(
    name = "EndTimes",
    version = "1.0",
    config = EndTimesConfig.class,
    listeners = {EndTimesListener.class}
)
@Data
@EqualsAndHashCode(callSuper = true)
public class EndTimes extends GameModeTemplate implements GameMode {
}
