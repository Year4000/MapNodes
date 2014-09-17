package net.year4000.mapnodes.gamemodes.magewars;

import lombok.Data;
import net.year4000.mapnodes.api.game.modes.GameModeConfig;
import net.year4000.mapnodes.api.game.modes.GameModeConfigName;
import net.year4000.mapnodes.exceptions.InvalidJsonException;

@Data
@GameModeConfigName("mage_wars")
public class MageWarsConfig implements GameModeConfig {
    @Override
    public void validate() throws InvalidJsonException {
    }
}
