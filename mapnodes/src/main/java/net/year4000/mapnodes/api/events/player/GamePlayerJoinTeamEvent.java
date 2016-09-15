/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.api.events.player;

import net.year4000.mapnodes.api.events.EventPlus;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.GameTeam;
import org.bukkit.event.Cancellable;

/** The event that gets called when a player join's a new team */
public class GamePlayerJoinTeamEvent extends EventPlus implements Cancellable {
    private final GamePlayer player;
    private final GameTeam from;
    private GameTeam to;
    private boolean joining;
    private boolean cancelled;
    private boolean display;

    public GamePlayerJoinTeamEvent(GamePlayer player, GameTeam from) {
        this.player = player;
        this.from = from;
    }

    @java.beans.ConstructorProperties({"player", "from", "to", "joining", "cancelled", "display"})
    public GamePlayerJoinTeamEvent(GamePlayer player, GameTeam from, GameTeam to, boolean joining, boolean cancelled, boolean display) {
        this.player = player;
        this.from = from;
        this.to = to;
        this.joining = joining;
        this.cancelled = cancelled;
        this.display = display;
    }

    public GamePlayer getPlayer() {
        return this.player;
    }

    public GameTeam getFrom() {
        return this.from;
    }

    public GameTeam getTo() {
        return this.to;
    }

    public boolean isJoining() {
        return this.joining;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public boolean isDisplay() {
        return this.display;
    }

    public void setTo(GameTeam to) {
        this.to = to;
    }

    public void setJoining(boolean joining) {
        this.joining = joining;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    public String toString() {
        return "net.year4000.mapnodes.api.events.player.GamePlayerJoinTeamEvent(player=" + this.getPlayer() + ", from=" + this.getFrom() + ", to=" + this.getTo() + ", joining=" + this.isJoining() + ", cancelled=" + this.isCancelled() + ", display=" + this.isDisplay() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof GamePlayerJoinTeamEvent)) return false;
        final GamePlayerJoinTeamEvent other = (GamePlayerJoinTeamEvent) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$player = this.getPlayer();
        final Object other$player = other.getPlayer();
        if (this$player == null ? other$player != null : !this$player.equals(other$player)) return false;
        final Object this$from = this.getFrom();
        final Object other$from = other.getFrom();
        if (this$from == null ? other$from != null : !this$from.equals(other$from)) return false;
        final Object this$to = this.getTo();
        final Object other$to = other.getTo();
        if (this$to == null ? other$to != null : !this$to.equals(other$to)) return false;
        if (this.isJoining() != other.isJoining()) return false;
        if (this.isCancelled() != other.isCancelled()) return false;
        if (this.isDisplay() != other.isDisplay()) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $player = this.getPlayer();
        result = result * PRIME + ($player == null ? 43 : $player.hashCode());
        final Object $from = this.getFrom();
        result = result * PRIME + ($from == null ? 43 : $from.hashCode());
        final Object $to = this.getTo();
        result = result * PRIME + ($to == null ? 43 : $to.hashCode());
        result = result * PRIME + (this.isJoining() ? 79 : 97);
        result = result * PRIME + (this.isCancelled() ? 79 : 97);
        result = result * PRIME + (this.isDisplay() ? 79 : 97);
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof GamePlayerJoinTeamEvent;
    }
}
