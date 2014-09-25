package net.year4000.mapnodes.gamemodes.deathmatch;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.game.NodeGame;
import net.year4000.mapnodes.gamemodes.GameModeTemplate;

@GameModeInfo(
    name = "Deathmatch",
    version = "1.0",
    config = DeathmatchConfig.class,
    listeners = {DeathmatchListener.class}
)
@Data
@EqualsAndHashCode(callSuper = true)
public class Deathmatch extends GameModeTemplate implements GameMode {
    private long startTime;
    private long endTime;

    public void setUpGoals() {
        MapNodes.getCurrentGame().getPlayingTeams().forEach(team -> MapNodes.getCurrentGame().addDynamicGoal(team.getId(), team.getDisplayName(), 0));
    }
}
