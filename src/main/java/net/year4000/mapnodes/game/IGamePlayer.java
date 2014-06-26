package net.year4000.mapnodes.game;

import lombok.Data;
import net.year4000.mapnodes.api.game.GamePlayer;
import org.bukkit.entity.Player;

@Data
public class IGamePlayer implements GamePlayer {
    private Player player;

    /** Constructs a game player */
    public IGamePlayer(Player player) {
        this.player = player;
    }

}
