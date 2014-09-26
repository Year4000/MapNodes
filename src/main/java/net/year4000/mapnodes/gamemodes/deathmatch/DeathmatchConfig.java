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
    private Integer timeLimit = null;

    @SerializedName("max_score")
    private Integer maxScore = null;

    @Override
    public void validate() throws InvalidJsonException {
        if (timeLimit != null) {
            checkArgument(timeLimit > 0);
            checkArgument(maxScore != null);
        }

        if (maxScore != null) {
            checkArgument(maxScore > 0);
        }
    }
}
