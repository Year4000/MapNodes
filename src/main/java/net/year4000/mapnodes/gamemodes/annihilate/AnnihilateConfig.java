package net.year4000.mapnodes.gamemodes.annihilate;

import lombok.Data;
import net.year4000.mapnodes.api.game.modes.GameModeConfig;
import net.year4000.mapnodes.api.game.modes.GameModeConfigName;
import net.year4000.mapnodes.exceptions.InvalidJsonException;

import static com.google.common.base.Preconditions.checkArgument;

@Data
@GameModeConfigName("annihilate")
public class AnnihilateConfig implements GameModeConfig {
    @Override
    public void validate() throws InvalidJsonException {
    }
}
