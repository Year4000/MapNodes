package net.year4000.mapnodes.gamemodes.endtimes;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.api.events.game.GameLoadEvent;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.gamemodes.GameModeTemplate;
import org.bukkit.event.EventHandler;

@GameModeInfo(
    name = "EndTimes",
    version = "1.0",
    config = EndTimesConfig.class
)
@Data
@EqualsAndHashCode(callSuper = true)
public class EndTimes extends GameModeTemplate implements GameMode {
    private EndTimesConfig gameModeConfig;

    @EventHandler
    public void onLoad(GameLoadEvent event) {
        gameModeConfig = (EndTimesConfig) getConfig();
    }
}
