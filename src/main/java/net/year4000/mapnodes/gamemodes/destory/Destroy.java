package net.year4000.mapnodes.gamemodes.destory;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.gamemodes.GameModeTemplate;

@GameModeInfo(
    name = "Destroy",
    version = "1.0",
    config = DestroyConfig.class,
    listeners = {DestroyListener.class}
)
@Data
@EqualsAndHashCode(callSuper = true)
public class Destroy extends GameModeTemplate implements GameMode {
}
