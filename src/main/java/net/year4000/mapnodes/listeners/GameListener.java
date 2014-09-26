package net.year4000.mapnodes.listeners;

import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.events.game.GameClockEvent;
import net.year4000.mapnodes.api.events.game.GameWinEvent;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.game.NodeGame;
import net.year4000.mapnodes.game.NodeKit;
import net.year4000.mapnodes.game.NodePlayer;
import net.year4000.mapnodes.game.NodeTeam;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Common;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.stream.Stream;

public final class GameListener implements Listener {
    @EventHandler
    public void respawn(PlayerRespawnEvent event) {
        GamePlayer player = MapNodes.getCurrentGame().getPlayer(event.getPlayer());

        if (player.isPlaying()) {
            ((NodeKit) player.getTeam().getKit()).giveKit(player);
            ((NodePlayer) player).updateInventory();

            // God buffer mode
            player.getPlayerTasks().add(NodeKit.immortal(event.getPlayer()));

            event.setRespawnLocation(((NodeTeam) player.getTeam()).getSafeRandomSpawn());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWin(GameWinEvent event) {
        final int size = 45;
        // Send messages to players before game end
        // Spectator Messages
        Stream.concat(event.getGame().getSpectating(), event.getGame().getEntering()).forEach(player -> {
            player.sendMessage("");
            player.sendMessage(Common.textLine(Msg.locale(player, "game.end"), 40, '*'));
            player.sendMessage(Common.textLine(Msg.locale(player, "game.end.winner", event.getWinnerText()), size, ' ', "", "&a"));
            player.sendMessage("&7&m******************************************");
            player.sendMessage("");
        });

        // Game Player Messages
        event.getGame().getPlaying().forEach(player -> {
            player.sendMessage("");
            player.sendMessage(Common.textLine(Msg.locale(player, "game.end"), 40, '*'));
            player.sendMessage(Common.textLine(Msg.locale(player, "game.end.winner", event.getWinnerText()), size, ' ', "", "&a"));

            if (event.getMessage().size() > 0) {
                player.sendMessage("");
                event.getMessage().forEach(string -> player.sendMessage(Common.textLine(string, size, ' ', "", "&a&o")));;
            }

            player.sendMessage("&7&m******************************************");
            player.sendMessage("");
        });

        // Stop the game
        ((NodeGame) MapNodes.getCurrentGame()).stop();
    }

    @EventHandler
    public void onClock(GameClockEvent event) {
        // If not in debug mode check if their are still players.
        if (!MapNodesPlugin.getInst().getLog().isDebug()) {
            // See if we should handle stop cases like this
            // ((NodeGame) event.getGame()).getStartControls().stream().filter(c -> !c.handle()).count() == ((NodeGame) event.getGame()).getStartControls().size()
            if (event.getGame().getPlaying().count() == 0) {
                ((NodeGame) event.getGame()).stop();
            }
        }
    }

    /** The world height cap. */
    @EventHandler(ignoreCancelled = true)
    public void onHeight(BlockPlaceEvent event) {
        int height = MapNodes.getCurrentGame().getConfig().getWorldHeight();

        if (height > 0) {
            int y = event.getBlockPlaced().getY();

            if (y >= height) {
                event.getPlayer().sendMessage(Msg.locale(event.getPlayer(), "region.deny.height"));
                event.setCancelled(true);
            }
        }
    }
}
