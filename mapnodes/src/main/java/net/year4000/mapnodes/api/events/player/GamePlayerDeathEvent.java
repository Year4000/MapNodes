/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.api.events.player;

import net.year4000.mapnodes.api.events.EventPlus;
import net.year4000.mapnodes.api.game.GamePlayer;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashSet;
import java.util.Set;

public class GamePlayerDeathEvent extends EventPlus {
    private Set<GamePlayer> viewers = new HashSet<>();
    private PlayerDeathEvent rawEvent;

    public GamePlayerDeathEvent(PlayerDeathEvent event) {
        rawEvent = event;
    }

    public Set<GamePlayer> getViewers() {
        return this.viewers;
    }

    public PlayerDeathEvent getRawEvent() {
        return this.rawEvent;
    }

    public void setRawEvent(PlayerDeathEvent rawEvent) {
        this.rawEvent = rawEvent;
    }

    public String toString() {
        return "net.year4000.mapnodes.api.events.player.GamePlayerDeathEvent(viewers=" + this.getViewers() + ", rawEvent=" + this.getRawEvent() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof GamePlayerDeathEvent)) return false;
        final GamePlayerDeathEvent other = (GamePlayerDeathEvent) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$viewers = this.getViewers();
        final Object other$viewers = other.getViewers();
        if (this$viewers == null ? other$viewers != null : !this$viewers.equals(other$viewers)) return false;
        final Object this$rawEvent = this.getRawEvent();
        final Object other$rawEvent = other.getRawEvent();
        if (this$rawEvent == null ? other$rawEvent != null : !this$rawEvent.equals(other$rawEvent)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $viewers = this.getViewers();
        result = result * PRIME + ($viewers == null ? 43 : $viewers.hashCode());
        final Object $rawEvent = this.getRawEvent();
        result = result * PRIME + ($rawEvent == null ? 43 : $rawEvent.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof GamePlayerDeathEvent;
    }
}
