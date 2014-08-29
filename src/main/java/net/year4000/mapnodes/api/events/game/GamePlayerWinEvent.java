package net.year4000.mapnodes.api.events.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.api.game.GamePlayer;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GamePlayerWinEvent extends GameWinEvent {
    private GamePlayer winner;
}
