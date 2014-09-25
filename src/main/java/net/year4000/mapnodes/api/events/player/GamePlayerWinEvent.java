package net.year4000.mapnodes.api.events.player;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.api.events.game.GameWinEvent;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.api.game.GamePlayer;

@Data
@EqualsAndHashCode(callSuper = false)
public class GamePlayerWinEvent extends GameWinEvent {
    private GamePlayer winner;

    public GamePlayerWinEvent(GameManager game, GamePlayer winner) {
        this.winner = winner;
        this.winnerText = winner.getPlayerColor();
        this.game = game;
    }
}
