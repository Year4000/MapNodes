package net.year4000.mapnodes.gamemodes.skywars;

import lombok.Data;
import net.year4000.mapnodes.api.game.modes.GameModeConfig;
import net.year4000.mapnodes.api.game.modes.GameModeConfigName;
import net.year4000.mapnodes.exceptions.InvalidJsonException;

import static com.google.common.base.Preconditions.checkArgument;

@Data
@GameModeConfigName("skywars")
public class SkywarsConfig implements GameModeConfig {
    @Override
    public void validate() throws InvalidJsonException {
    }
}
