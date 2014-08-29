package net.year4000.mapnodes.listeners;

import net.year4000.mapnodes.NodeFactory;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.game.MatchManager;
import net.year4000.mapnodes.game.NodeGame;
import net.year4000.mapnodes.game.components.NodeMap;
import net.year4000.mapnodes.utils.Common;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.utilities.ChatColor;
import net.year4000.utilities.bukkit.FunEffectsUtil;
import net.year4000.utilities.bukkit.LocationUtil;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;

public final class MapNodesListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        ((NodeGame) MapNodes.getCurrentGame()).join(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        ((NodeGame) MapNodes.getCurrentGame()).quit(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        event.setRespawnLocation(LocationUtil.center(event.getRespawnLocation().clone()));

        MapNodes.getCurrentGame().getPlayer(event.getPlayer()).getPlayerTasks().add(SchedulerUtil.runSync(() -> {
            FunEffectsUtil.playSound(event.getPlayer(), Sound.ENDERMAN_TELEPORT);
            FunEffectsUtil.playEffect(event.getPlayer(), Effect.ENDER_SIGNAL);
        }, 1));
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.PLUGIN) return;

        event.setTo(LocationUtil.center(event.getTo().clone()));

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
        GameManager gm = MapNodes.getCurrentGame();
        MatchManager mm = NodeFactory.get().getCurrentGame().getMatch();

        event.setNumPlayers((int) gm.getPlayers().filter(GamePlayer::isPlaying).count());
        event.setMaxPlayers(gm.getMaxPlayers());

        event.setMotd(MessageUtil.message(
            "%s%s &7| &5&o%s &7%s \n&f%s",
            gm.getStage().getStageColor(),
            gm.getStage(),
            gm.getMap().getName(),
            Common.formatSeparators(gm.getMap().getVersion(), ChatColor.GRAY, ChatColor.DARK_GRAY),
            ((NodeMap) gm.getMap()).getShortDescription(45)
        ));

        if (mm.getIcon() != null) {
            event.setServerIcon(mm.getIcon());
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
}
