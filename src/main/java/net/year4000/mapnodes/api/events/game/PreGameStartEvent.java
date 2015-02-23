package net.year4000.mapnodes.api.events.game;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.api.events.EventPlus;
import net.year4000.mapnodes.api.game.GameManager;

@Data
@EqualsAndHashCode(callSuper = false)
/** The event that gets called before the game has started */
public class PreGameStartEvent extends EventPlus {
    private final GameManager game;

    public PreGameStartEvent(GameManager game) {
        this.game = game;
    }
}
