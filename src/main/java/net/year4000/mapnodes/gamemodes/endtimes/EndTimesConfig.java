package net.year4000.mapnodes.gamemodes.endtimes;

import lombok.Data;
import net.year4000.mapnodes.api.game.modes.GameModeConfig;
import net.year4000.mapnodes.api.game.modes.GameModeConfigName;
import net.year4000.mapnodes.exceptions.InvalidJsonException;

import static com.google.common.base.Preconditions.checkArgument;

@Data
@GameModeConfigName("end_times")
public class EndTimesConfig implements GameModeConfig {
    @Override
    public void validate() throws InvalidJsonException {
    }
}
