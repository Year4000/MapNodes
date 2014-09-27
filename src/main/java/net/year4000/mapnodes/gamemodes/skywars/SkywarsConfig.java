package net.year4000.mapnodes.gamemodes.skywars;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import net.year4000.mapnodes.api.game.modes.GameModeConfig;
import net.year4000.mapnodes.api.game.modes.GameModeConfigName;
import net.year4000.mapnodes.exceptions.InvalidJsonException;
import net.year4000.mapnodes.messages.Msg;

import static com.google.common.base.Preconditions.checkArgument;

@Data
@GameModeConfigName("skywars")
public class SkywarsConfig implements GameModeConfig {
    @SerializedName("players_team")
    private String playersTeam = null;

    @Override
    public void validate() throws InvalidJsonException {
        checkArgument(playersTeam != null, Msg.util("skywars.error.team_null"));
    }
}
