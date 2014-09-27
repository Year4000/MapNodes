package net.year4000.mapnodes.gamemodes.destory;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.api.events.game.GameLoadEvent;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.gamemodes.GameModeTemplate;
import org.bukkit.event.EventHandler;

@GameModeInfo(
    name = "Destroy",
    version = "1.0",
    config = DestroyConfig.class
)
@Data
@EqualsAndHashCode(callSuper = true)
public class Destroy extends GameModeTemplate implements GameMode {
    private DestroyConfig gameModeConfig;

    @EventHandler
    public void onLoad(GameLoadEvent event) {
        gameModeConfig = (DestroyConfig) getConfig();
    }
}
