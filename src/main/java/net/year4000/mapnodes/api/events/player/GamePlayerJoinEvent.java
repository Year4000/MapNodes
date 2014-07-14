package net.year4000.mapnodes.api.events.player;

import lombok.*;
import net.year4000.mapnodes.api.events.EventPlus;
import net.year4000.mapnodes.api.game.GamePlayer;
import org.bukkit.Location;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
/** The event that gets called when a player joins the game */
public class GamePlayerJoinEvent extends EventPlus {
    private final GamePlayer player;
    private Location spawn;
    private boolean menu;

    public GamePlayerJoinEvent(GamePlayer player) {
        this.player = player;
    }
}
