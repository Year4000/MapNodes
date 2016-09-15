/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.api.events.game;

import net.year4000.mapnodes.api.events.EventPlus;
import net.year4000.mapnodes.api.game.GameManager;

import java.util.ArrayList;
import java.util.List;

public class GameWinEvent extends EventPlus {
    protected GameManager game;
    protected String winnerText;
    protected List<String> message = new ArrayList<>();

    public GameWinEvent() {
    }

    public GameManager getGame() {
        return this.game;
    }

    public String getWinnerText() {
        return this.winnerText;
    }

    public List<String> getMessage() {
        return this.message;
    }

    public void setGame(GameManager game) {
        this.game = game;
    }

    public void setWinnerText(String winnerText) {
        this.winnerText = winnerText;
    }

    public void setMessage(List<String> message) {
        this.message = message;
    }

    public String toString() {
        return "net.year4000.mapnodes.api.events.game.GameWinEvent(game=" + this.getGame() + ", winnerText=" + this.getWinnerText() + ", message=" + this.getMessage() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof GameWinEvent)) return false;
        final GameWinEvent other = (GameWinEvent) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$game = this.getGame();
        final Object other$game = other.getGame();
        if (this$game == null ? other$game != null : !this$game.equals(other$game)) return false;
        final Object this$winnerText = this.getWinnerText();
        final Object other$winnerText = other.getWinnerText();
        if (this$winnerText == null ? other$winnerText != null : !this$winnerText.equals(other$winnerText))
            return false;
        final Object this$message = this.getMessage();
        final Object other$message = other.getMessage();
        if (this$message == null ? other$message != null : !this$message.equals(other$message)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $game = this.getGame();
        result = result * PRIME + ($game == null ? 43 : $game.hashCode());
        final Object $winnerText = this.getWinnerText();
        result = result * PRIME + ($winnerText == null ? 43 : $winnerText.hashCode());
        final Object $message = this.getMessage();
        result = result * PRIME + ($message == null ? 43 : $message.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof GameWinEvent;
    }
}
