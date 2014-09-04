package net.year4000.mapnodes.gamemodes.skywars;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.gamemodes.GameModeTemplate;

@GameModeInfo(
    name = "Skywars",
    version = "1.0",
    config = SkywarsConfig.class,
    listeners = {SkywarsListener.class}
)
@Data
@EqualsAndHashCode(callSuper = true)
public class Skywars extends GameModeTemplate implements GameMode {
}
