package net.year4000.mapnodes.gamemodes.juggernaut;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.api.events.game.GameLoadEvent;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.gamemodes.GameModeTemplate;
import org.bukkit.event.EventHandler;

@GameModeInfo(
    name = "Juggernaut",
    version = "1.0",
    config = JuggernautConfig.class
)
@Data
@EqualsAndHashCode(callSuper = true)
public class Juggernaut extends GameModeTemplate implements GameMode {
    private JuggernautConfig gameModeConfig;

    @EventHandler
    public void onLoad(GameLoadEvent event) {
        gameModeConfig = (JuggernautConfig) getConfig();
    }
}
