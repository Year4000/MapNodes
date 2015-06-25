/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.api.events.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.api.events.EventPlus;
import net.year4000.mapnodes.api.game.GameManager;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GameClockEvent extends EventPlus {
    private GameManager game;
}
