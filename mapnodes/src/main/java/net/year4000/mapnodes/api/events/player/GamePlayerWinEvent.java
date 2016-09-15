/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.api.events.player;

import net.year4000.mapnodes.api.events.game.GameWinEvent;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.api.game.GamePlayer;

public class GamePlayerWinEvent extends GameWinEvent {
    private GamePlayer winner;

    public GamePlayerWinEvent(GameManager game, GamePlayer winner) {
        this.winner = winner;
        this.winnerText = winner.getPlayerColor();
        this.game = game;
    }

    public GamePlayer getWinner() {
        return this.winner;
    }

    public void setWinner(GamePlayer winner) {
        this.winner = winner;
    }

    public String toString() {
        return "net.year4000.mapnodes.api.events.player.GamePlayerWinEvent(winner=" + this.getWinner() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof GamePlayerWinEvent)) return false;
        final GamePlayerWinEvent other = (GamePlayerWinEvent) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$winner = this.getWinner();
        final Object other$winner = other.getWinner();
        if (this$winner == null ? other$winner != null : !this$winner.equals(other$winner)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $winner = this.getWinner();
        result = result * PRIME + ($winner == null ? 43 : $winner.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof GamePlayerWinEvent;
    }
}
