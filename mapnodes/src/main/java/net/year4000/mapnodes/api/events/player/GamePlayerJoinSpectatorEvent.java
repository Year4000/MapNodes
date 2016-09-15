/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.api.events.player;

import net.year4000.mapnodes.api.events.EventPlus;
import net.year4000.mapnodes.api.game.GameKit;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.GameTeam;

public class GamePlayerJoinSpectatorEvent extends EventPlus {
    private final GamePlayer player;
    private final GameTeam spectator;
    private GameKit kit;
    private boolean display;

    public GamePlayerJoinSpectatorEvent(GamePlayer player, GameTeam spectator) {
        this.player = player;
        this.spectator = spectator;
    }

    @java.beans.ConstructorProperties({"player", "spectator", "kit", "display"})
    public GamePlayerJoinSpectatorEvent(GamePlayer player, GameTeam spectator, GameKit kit, boolean display) {
        this.player = player;
        this.spectator = spectator;
        this.kit = kit;
        this.display = display;
    }

    public GamePlayer getPlayer() {
        return this.player;
    }

    public GameTeam getSpectator() {
        return this.spectator;
    }

    public GameKit getKit() {
        return this.kit;
    }

    public boolean isDisplay() {
        return this.display;
    }

    public void setKit(GameKit kit) {
        this.kit = kit;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    public String toString() {
        return "net.year4000.mapnodes.api.events.player.GamePlayerJoinSpectatorEvent(player=" + this.getPlayer() + ", spectator=" + this.getSpectator() + ", kit=" + this.getKit() + ", display=" + this.isDisplay() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof GamePlayerJoinSpectatorEvent)) return false;
        final GamePlayerJoinSpectatorEvent other = (GamePlayerJoinSpectatorEvent) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$player = this.getPlayer();
        final Object other$player = other.getPlayer();
        if (this$player == null ? other$player != null : !this$player.equals(other$player)) return false;
        final Object this$spectator = this.getSpectator();
        final Object other$spectator = other.getSpectator();
        if (this$spectator == null ? other$spectator != null : !this$spectator.equals(other$spectator)) return false;
        final Object this$kit = this.getKit();
        final Object other$kit = other.getKit();
        if (this$kit == null ? other$kit != null : !this$kit.equals(other$kit)) return false;
        if (this.isDisplay() != other.isDisplay()) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $player = this.getPlayer();
        result = result * PRIME + ($player == null ? 43 : $player.hashCode());
        final Object $spectator = this.getSpectator();
        result = result * PRIME + ($spectator == null ? 43 : $spectator.hashCode());
        final Object $kit = this.getKit();
        result = result * PRIME + ($kit == null ? 43 : $kit.hashCode());
        result = result * PRIME + (this.isDisplay() ? 79 : 97);
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof GamePlayerJoinSpectatorEvent;
    }
}
