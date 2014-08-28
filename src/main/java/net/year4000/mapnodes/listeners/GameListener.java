package net.year4000.mapnodes.listeners;

import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.game.components.NodeKit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public final class GameListener implements Listener {

    @EventHandler
    public void respawn(PlayerRespawnEvent e) {
        GamePlayer player = MapNodes.getCurrentGame().getPlayer(e.getPlayer());

        if (player.isPlaying()) {
            ((NodeKit) player.getTeam().getKit()).giveKit(player);

            // God buffer mode
            player.getPlayerTasks().addAll(NodeKit.immortal(e.getPlayer()));
        }
    }
}
