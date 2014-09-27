package net.year4000.mapnodes.gamemodes.annihilate;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.api.events.game.GameLoadEvent;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.gamemodes.GameModeTemplate;
import org.bukkit.event.EventHandler;

@GameModeInfo(
    name = "Annihilate",
    version = "1.0",
    config = AnnihilateConfig.class
)
@Data
@EqualsAndHashCode(callSuper = true)
public class Annihilate extends GameModeTemplate implements GameMode {
    private AnnihilateConfig gameModeConfig;

    @EventHandler
    public void onLoad(GameLoadEvent event) {
        gameModeConfig = (AnnihilateConfig) getConfig();
    }
}
