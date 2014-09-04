package net.year4000.mapnodes.gamemodes.capture;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.gamemodes.GameModeTemplate;

@GameModeInfo(
    name = "Capture",
    version = "1.0",
    config = CaptureConfig.class,
    listeners = {CaptureListener.class}
)
@Data
@EqualsAndHashCode(callSuper = true)
public class Capture extends GameModeTemplate implements GameMode {
}
