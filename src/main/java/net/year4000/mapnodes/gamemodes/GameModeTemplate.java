package net.year4000.mapnodes.gamemodes;

import lombok.Data;
import net.year4000.mapnodes.api.game.modes.GameModeConfig;

@Data
public abstract class GameModeTemplate {
    private GameModeConfig config;
}
