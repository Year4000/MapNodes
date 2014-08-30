package net.year4000.mapnodes.api.events.player;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import net.year4000.mapnodes.api.events.EventPlus;
import net.year4000.mapnodes.api.game.GamePlayer;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
public class GamePlayerDeathEvent extends EventPlus {
    @Setter(AccessLevel.NONE)
    private Set<GamePlayer> viewers = new HashSet<>();
    private PlayerDeathEvent rawEvent;

    public GamePlayerDeathEvent(PlayerDeathEvent event) {
        rawEvent = event;
    }
}
