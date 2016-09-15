/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.api.events.game;

import net.year4000.mapnodes.api.events.EventPlus;
import net.year4000.mapnodes.api.game.GameManager;

public class GameLoadEvent extends EventPlus {
    private GameManager game;

    @java.beans.ConstructorProperties({"game"})
    public GameLoadEvent(GameManager game) {
        this.game = game;
    }

    public GameManager getGame() {
        return this.game;
    }

    public void setGame(GameManager game) {
        this.game = game;
    }

    public String toString() {
        return "net.year4000.mapnodes.api.events.game.GameLoadEvent(game=" + this.getGame() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof GameLoadEvent)) return false;
        final GameLoadEvent other = (GameLoadEvent) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$game = this.getGame();
        final Object other$game = other.getGame();
        if (this$game == null ? other$game != null : !this$game.equals(other$game)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $game = this.getGame();
        result = result * PRIME + ($game == null ? 43 : $game.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof GameLoadEvent;
    }
}
