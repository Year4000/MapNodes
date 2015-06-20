/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.api.events.team;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.api.events.game.GameWinEvent;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.api.game.GameTeam;

@Data
@EqualsAndHashCode(callSuper = false)
public class GameTeamWinEvent extends GameWinEvent {
    private GameTeam winner;

    public GameTeamWinEvent(GameManager game, GameTeam winner) {
        this.winner = winner;
        this.winnerText = winner == null ? "" : winner.getColor().toString() + winner.getName();
        this.game = game;
    }
}
