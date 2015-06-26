/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.games.elimination;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import net.year4000.mapnodes.api.exceptions.InvalidJsonException;
import net.year4000.mapnodes.api.game.modes.GameModeConfig;
import net.year4000.mapnodes.api.game.modes.GameModeConfigName;
import net.year4000.mapnodes.messages.Msg;

import static com.google.common.base.Preconditions.checkArgument;

@Data
@GameModeConfigName("elimination")
public class EliminationConfig implements GameModeConfig {
    @SerializedName("players_team")
    private String playersTeam = null;

    @SerializedName("start_size")
    private int startSize = 2;

    @Override
    public void validate() throws InvalidJsonException {
        checkArgument(startSize >= 2, Msg.util("elimination.start_size"));
    }
}
