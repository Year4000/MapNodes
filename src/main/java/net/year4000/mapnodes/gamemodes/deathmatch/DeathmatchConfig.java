package net.year4000.mapnodes.gamemodes.deathmatch;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import net.year4000.mapnodes.api.game.modes.GameModeConfig;
import net.year4000.mapnodes.api.game.modes.GameModeConfigName;
import net.year4000.mapnodes.exceptions.InvalidJsonException;

import static com.google.common.base.Preconditions.checkArgument;

@Data
@GameModeConfigName("deathmatch")
public class DeathmatchConfig implements GameModeConfig {
    @SerializedName("time_limit")
    private int timeLimit = 60;

    @Override
    public void validate() throws InvalidJsonException {
        checkArgument(timeLimit > 0);
    }
}
