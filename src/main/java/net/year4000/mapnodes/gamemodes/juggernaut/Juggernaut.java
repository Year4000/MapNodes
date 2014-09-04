package net.year4000.mapnodes.gamemodes.juggernaut;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.gamemodes.GameModeTemplate;

@GameModeInfo(
    name = "Juggernaut",
    version = "1.0",
    config = JuggernautConfig.class,
    listeners = {JuggernautListener.class}
)
@Data
@EqualsAndHashCode(callSuper = true)
public class Juggernaut extends GameModeTemplate implements GameMode {
}
