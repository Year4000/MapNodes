/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.api.events.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.api.events.EventPlus;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.GameTeam;
import org.bukkit.event.Cancellable;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
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
}
