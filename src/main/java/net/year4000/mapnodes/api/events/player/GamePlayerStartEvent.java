package net.year4000.mapnodes.api.events.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.api.events.EventPlus;
import net.year4000.mapnodes.api.game.GameKit;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.GameTeam;
import org.bukkit.Location;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
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

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
