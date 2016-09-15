/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.api.events.player;

import net.year4000.mapnodes.api.events.EventPlus;
import net.year4000.mapnodes.api.game.GamePlayer;
import org.bukkit.Location;

/** The event that gets called when a player joins the game */
public class GamePlayerJoinEvent extends EventPlus {
    private final GamePlayer player;
    private Location spawn;
    private boolean menu;

    public GamePlayerJoinEvent(GamePlayer player) {
        this.player = player;
    }

    @java.beans.ConstructorProperties({"player", "spawn", "menu"})
    public GamePlayerJoinEvent(GamePlayer player, Location spawn, boolean menu) {
        this.player = player;
        this.spawn = spawn;
        this.menu = menu;
    }

    public GamePlayer getPlayer() {
        return this.player;
    }

    public Location getSpawn() {
        return this.spawn;
    }

    public boolean isMenu() {
        return this.menu;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public void setMenu(boolean menu) {
        this.menu = menu;
    }

    public String toString() {
        return "net.year4000.mapnodes.api.events.player.GamePlayerJoinEvent(player=" + this.getPlayer() + ", spawn=" + this.getSpawn() + ", menu=" + this.isMenu() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof GamePlayerJoinEvent)) return false;
        final GamePlayerJoinEvent other = (GamePlayerJoinEvent) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$player = this.getPlayer();
        final Object other$player = other.getPlayer();
        if (this$player == null ? other$player != null : !this$player.equals(other$player)) return false;
        final Object this$spawn = this.getSpawn();
        final Object other$spawn = other.getSpawn();
        if (this$spawn == null ? other$spawn != null : !this$spawn.equals(other$spawn)) return false;
        if (this.isMenu() != other.isMenu()) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $player = this.getPlayer();
        result = result * PRIME + ($player == null ? 43 : $player.hashCode());
        final Object $spawn = this.getSpawn();
        result = result * PRIME + ($spawn == null ? 43 : $spawn.hashCode());
        result = result * PRIME + (this.isMenu() ? 79 : 97);
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof GamePlayerJoinEvent;
    }
}
