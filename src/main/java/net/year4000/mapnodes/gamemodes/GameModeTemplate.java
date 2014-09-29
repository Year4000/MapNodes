package net.year4000.mapnodes.gamemodes;

import lombok.Getter;
import lombok.Setter;
import net.year4000.mapnodes.api.game.modes.GameModeConfig;

public abstract class GameModeTemplate {
    @Getter
    @Setter
    private transient GameModeConfig config;
}
