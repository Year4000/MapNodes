package net.year4000.mapnodes.listeners;

import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.NodeFactory;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.events.game.GameClockEvent;
import net.year4000.mapnodes.api.events.game.GameStopEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerJoinSpectatorEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerJoinTeamEvent;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.clocks.StartGame;
import net.year4000.mapnodes.game.Node;
import net.year4000.mapnodes.game.NodeGame;
import net.year4000.mapnodes.game.system.Spectator;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Common;
import net.year4000.mapnodes.utils.MathUtil;
import net.year4000.mapnodes.utils.PacketHacks;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.utilities.bukkit.FunEffectsUtil;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;

@EqualsAndHashCode
public final class MapNodesListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerLoginEvent event) {
        NodeGame game = (NodeGame) MapNodes.getCurrentGame();
        if (game.getPlayers().count() > game.getMaxPlayers() + game.getMaxPlayers() / 2) {
            event.disallow(PlayerLoginEvent.Result.KICK_FULL, Msg.locale(event.getPlayer(), "server.full") + '\n' + Msg.locale(event.getPlayer(), "team.select.non_vip_url"));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        ((NodeGame) MapNodes.getCurrentGame()).join(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        ((NodeGame) MapNodes.getCurrentGame()).quit(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        event.setRespawnLocation(Common.center(event.getRespawnLocation()));

        MapNodes.getCurrentGame().getPlayer(event.getPlayer()).getPlayerTasks().add(SchedulerUtil.runSync(() -> {
            FunEffectsUtil.playSound(event.getPlayer(), Sound.ENDERMAN_TELEPORT);
            FunEffectsUtil.playEffect(event.getPlayer(), Effect.ENDER_SIGNAL);
        }, 1));
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.PLUGIN) return;

        event.setTo(Common.center(event.getTo()));

        MapNodes.getCurrentGame().getPlayer(event.getPlayer()).getPlayerTasks().add(SchedulerUtil.runSync(() -> {
            FunEffectsUtil.playSound(event.getPlayer(), Sound.ENDERMAN_TELEPORT);
            FunEffectsUtil.playEffect(event.getPlayer(), Effect.ENDER_SIGNAL);
        }, 1));
    }

    @EventHandler
    public void onAchievement(PlayerAchievementAwardedEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPing(ServerListPingEvent event) {
        Node node = NodeFactory.get().getCurrentGame();
        GameManager gm = node.getGame();

        // Don't show spectators when game is playing
        if (gm.getStage().isPlaying()) {
            event.setNumPlayers((int) gm.getPlayers().filter(GamePlayer::isPlaying).count());
        }
        // Show all players any time else
        else {
            event.setNumPlayers((int) gm.getPlayers().count());
        }

        event.setMaxPlayers(gm.getMaxPlayers());

        event.setMotd(MessageUtil.message(
            "%s%s &7| &5&o%s",
            gm.getStage().getStageColor(),
            gm.getStage(),
            gm.getMap().getName()
        ));

        if (node.getIcon() != null) {
            event.setServerIcon(node.getIcon());
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location loc = event.getTo().clone();
        loc.setYaw(0);
        loc.setPitch(0);

        player.setCompassTarget(loc);
    }

    /** No players stop the game unless in debug mode */
    @EventHandler
    public void onClock(GameClockEvent event) {
        NodeGame game = ((NodeGame) MapNodes.getCurrentGame());

        if (game.getPlayers().count() == 0L && !MapNodesPlugin.getInst().getLog().isDebug()) {
            game.stop();
        }
    }

    int lastSize = 0;

    /** Start the game and when another player join reduce the time */
    @EventHandler
    public void onClock(GamePlayerJoinTeamEvent event) {
        if (event.getTo() instanceof Spectator) return;
        if (!MapNodes.getCurrentGame().getStage().isPreGame()) return;

        NodeGame game = ((NodeGame) MapNodes.getCurrentGame());
        int size = (int) game.getEntering().count();
        boolean biggerThanLast = lastSize < size;
        lastSize = size;

        if (game.shouldStart()) {
            if (game.getStage().isStarting()) {
                if (game.getStartClock().getClock().getIndex() > MathUtil.ticks(30) && biggerThanLast) {
                    game.getStartClock().reduceTime(10); // 10 secs

                    // Announcer to players that time was reduce
                    game.getEntering().forEach(p -> p.sendMessage(Msg.locale(p, "clocks.start.reduce")));
                }
            } else if (game.getStage().isWaiting()) {
                new StartGame(120).run(); // 2 mins
            }
        }
    }

    /** Reset last size for next game */
    @EventHandler
    public void onEnd(GameStopEvent event) {
        lastSize = 0;
    }

    /** Force player respawn when joining spectators */
    @EventHandler
    public void forceRespawn(GamePlayerJoinSpectatorEvent event) {
        if (event.getPlayer().getPlayer().isDead()) {
            PacketHacks.respawnPlayer(event.getPlayer().getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        event.setCancelled(true);
    }
}
