package net.year4000.mapnodes.gamemodes.arrowtag;

import lombok.Data;
import net.year4000.mapnodes.api.game.modes.GameModeConfig;
import net.year4000.mapnodes.api.game.modes.GameModeConfigName;
import net.year4000.mapnodes.exceptions.InvalidJsonException;

@Data
@GameModeConfigName("arrow_tag")
public class ArrowTagConfig implements GameModeConfig {
    @Override
    public void validate() throws InvalidJsonException {
    }
}
