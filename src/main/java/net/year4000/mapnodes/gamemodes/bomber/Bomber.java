package net.year4000.mapnodes.gamemodes.bomber;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.api.events.game.GameLoadEvent;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.gamemodes.GameModeTemplate;
import org.bukkit.event.EventHandler;

@GameModeInfo(
    name = "Bommer",
    version = "1.0",
    config = BomberConfig.class
)
@Data
@EqualsAndHashCode(callSuper = true)
public class Bomber extends GameModeTemplate implements GameMode {
    private BomberConfig gameModeConfig;

    @EventHandler
    public void onLoad(GameLoadEvent event) {
        gameModeConfig = (BomberConfig) getConfig();
    }
}
