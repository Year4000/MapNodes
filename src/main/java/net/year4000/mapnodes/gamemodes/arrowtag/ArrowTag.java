package net.year4000.mapnodes.gamemodes.arrowtag;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.api.events.game.GameLoadEvent;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.gamemodes.GameModeTemplate;
import org.bukkit.event.EventHandler;

@GameModeInfo(
    name = "Arrow Tag",
    version = "1.0",
    config = ArrowTagConfig.class
)
@Data
@EqualsAndHashCode(callSuper = true)
public class ArrowTag extends GameModeTemplate implements GameMode {
    private ArrowTagConfig gameModeConfig;

    @EventHandler
    public void onLoad(GameLoadEvent event) {
        gameModeConfig = (ArrowTagConfig) getConfig();
    }
}
