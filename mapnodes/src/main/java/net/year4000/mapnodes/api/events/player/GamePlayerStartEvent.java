/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.api.events.player;

import net.year4000.mapnodes.api.events.EventPlus;
import net.year4000.mapnodes.api.game.GameKit;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.GameTeam;
import org.bukkit.Location;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

public class GamePlayerStartEvent extends EventPlus {
    private static final HandlerList handlers = new HandlerList();
    private final GamePlayer player;
    private GameTeam team;
    private GameKit kit;
    private Location spawn;
    private boolean immortal;
    private List<String> message = new ArrayList<>();

    public GamePlayerStartEvent(GamePlayer player) {
        this.player = player;
    }

    @java.beans.ConstructorProperties({"player", "team", "kit", "spawn", "immortal", "message"})
    public GamePlayerStartEvent(GamePlayer player, GameTeam team, GameKit kit, Location spawn, boolean immortal, List<String> message) {
        this.player = player;
        this.team = team;
        this.kit = kit;
        this.spawn = spawn;
        this.immortal = immortal;
        this.message = message;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public GamePlayer getPlayer() {
        return this.player;
    }

    public GameTeam getTeam() {
        return this.team;
    }

    public GameKit getKit() {
        return this.kit;
    }

    public Location getSpawn() {
        return this.spawn;
    }

    public boolean isImmortal() {
        return this.immortal;
    }

    public List<String> getMessage() {
        return this.message;
    }

    public void setTeam(GameTeam team) {
        this.team = team;
    }

    public void setKit(GameKit kit) {
        this.kit = kit;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public void setImmortal(boolean immortal) {
        this.immortal = immortal;
    }

    public void setMessage(List<String> message) {
        this.message = message;
    }

    public String toString() {
        return "net.year4000.mapnodes.api.events.player.GamePlayerStartEvent(player=" + this.getPlayer() + ", team=" + this.getTeam() + ", kit=" + this.getKit() + ", spawn=" + this.getSpawn() + ", immortal=" + this.isImmortal() + ", message=" + this.getMessage() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof GamePlayerStartEvent)) return false;
        final GamePlayerStartEvent other = (GamePlayerStartEvent) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$player = this.getPlayer();
        final Object other$player = other.getPlayer();
        if (this$player == null ? other$player != null : !this$player.equals(other$player)) return false;
        final Object this$team = this.getTeam();
        final Object other$team = other.getTeam();
        if (this$team == null ? other$team != null : !this$team.equals(other$team)) return false;
        final Object this$kit = this.getKit();
        final Object other$kit = other.getKit();
        if (this$kit == null ? other$kit != null : !this$kit.equals(other$kit)) return false;
        final Object this$spawn = this.getSpawn();
        final Object other$spawn = other.getSpawn();
        if (this$spawn == null ? other$spawn != null : !this$spawn.equals(other$spawn)) return false;
        if (this.isImmortal() != other.isImmortal()) return false;
        final Object this$message = this.getMessage();
        final Object other$message = other.getMessage();
        if (this$message == null ? other$message != null : !this$message.equals(other$message)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $player = this.getPlayer();
        result = result * PRIME + ($player == null ? 43 : $player.hashCode());
        final Object $team = this.getTeam();
        result = result * PRIME + ($team == null ? 43 : $team.hashCode());
        final Object $kit = this.getKit();
        result = result * PRIME + ($kit == null ? 43 : $kit.hashCode());
        final Object $spawn = this.getSpawn();
        result = result * PRIME + ($spawn == null ? 43 : $spawn.hashCode());
        result = result * PRIME + (this.isImmortal() ? 79 : 97);
        final Object $message = this.getMessage();
        result = result * PRIME + ($message == null ? 43 : $message.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof GamePlayerStartEvent;
    }
}
