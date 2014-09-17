package net.year4000.mapnodes.gamemodes.deathmatch;

import lombok.Data;
import net.year4000.mapnodes.api.game.modes.GameModeConfig;
import net.year4000.mapnodes.api.game.modes.GameModeConfigName;
import net.year4000.mapnodes.exceptions.InvalidJsonException;

@Data
@GameModeConfigName("deathmatch")
public class DeathmatchConfig implements GameModeConfig {
    @Override
    public void validate() throws InvalidJsonException {
    }
}
