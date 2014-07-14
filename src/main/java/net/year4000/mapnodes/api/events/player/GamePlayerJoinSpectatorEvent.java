package net.year4000.mapnodes.api.events.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.api.events.EventPlus;
import net.year4000.mapnodes.api.game.GameKit;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.GameTeam;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class GamePlayerJoinSpectatorEvent extends EventPlus {
    private final GamePlayer player;
    private final GameTeam spectator;
    private GameKit kit;
    private boolean display;

    public GamePlayerJoinSpectatorEvent(GamePlayer player, GameTeam spectator) {
        this.player = player;
        this.spectator = spectator;
    }
}
