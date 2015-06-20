/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.api.events.game;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.api.events.EventPlus;
import net.year4000.mapnodes.api.game.GameManager;

@Data
@EqualsAndHashCode(callSuper = false)
public class GameStopEvent extends EventPlus {
    private final GameManager game;

    public GameStopEvent(GameManager game) {
        this.game = game;
    }
}
