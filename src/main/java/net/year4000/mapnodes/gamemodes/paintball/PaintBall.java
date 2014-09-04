package net.year4000.mapnodes.gamemodes.paintball;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.gamemodes.GameModeTemplate;

@GameModeInfo(
    name = "PaintBall",
    version = "1.0",
    config = PaintBallConfig.class,
    listeners = {PaintBallListener.class}
)
@Data
@EqualsAndHashCode(callSuper = true)
public class PaintBall extends GameModeTemplate implements GameMode {
}
