package net.year4000.mapnodes.api.events.game;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.api.events.EventPlus;
import net.year4000.mapnodes.api.game.GameManager;

@Data
@EqualsAndHashCode(callSuper = false)
public class GameWinEvent extends EventPlus {
    protected GameManager game;
}
