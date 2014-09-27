package net.year4000.mapnodes.gamemodes.skywars;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.api.events.game.GameLoadEvent;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.gamemodes.GameModeTemplate;
import org.bukkit.event.EventHandler;

@GameModeInfo(
    name = "Skywars",
    version = "1.0",
    config = SkywarsConfig.class
)
@Data
@EqualsAndHashCode(callSuper = true)
public class Skywars extends GameModeTemplate implements GameMode {
    private SkywarsConfig gameModeConfig;

    @EventHandler
    public void onLoad(GameLoadEvent event) {
        gameModeConfig = (SkywarsConfig) getConfig();
    }
}
