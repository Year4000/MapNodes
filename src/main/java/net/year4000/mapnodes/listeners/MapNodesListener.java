package net.year4000.mapnodes.listeners;

import com.ewized.utilities.bukkit.util.FunEffectsUtil;
import com.ewized.utilities.bukkit.util.MessageUtil;
import com.ewized.utilities.core.util.*;
import com.ewized.utilities.core.util.ChatColor;
import net.year4000.mapnodes.NodeFactory;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.game.MatchManager;
import net.year4000.mapnodes.game.NodeGame;
import net.year4000.mapnodes.game.components.NodeMap;
import net.year4000.mapnodes.utils.Common;
import net.year4000.mapnodes.utils.SchedulerUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;

public final class MapNodesListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        ((NodeGame) MapNodes.getCurrentGame()).join(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        ((NodeGame) MapNodes.getCurrentGame()).quit(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent e) {
        Location to = e.getRespawnLocation().clone();

        to.setX(to.getBlockX() >= 0 ? to.getBlockX() + 0.5 : to.getBlockX() - 0.5);
        to.setZ(to.getBlockZ() >= 0 ? to.getBlockZ() + 0.5 : to.getBlockZ() - 0.5);

        e.setRespawnLocation(to);

        MapNodes.getCurrentGame().getPlayer(e.getPlayer()).getPlayerTasks().add(SchedulerUtil.runSync(() -> {
            FunEffectsUtil.playSound(e.getPlayer(), Sound.ENDERMAN_TELEPORT);
            FunEffectsUtil.playEffect(e.getPlayer(), Effect.ENDER_SIGNAL);
        }, 1));
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent e) {
        if (e.getCause() != PlayerTeleportEvent.TeleportCause.PLUGIN) return;

        Location to = e.getTo().clone();

        to.setX(to.getBlockX() >= 0 ? to.getBlockX() + 0.5 : to.getBlockX() - 0.5);
        to.setZ(to.getBlockZ() >= 0 ? to.getBlockZ() + 0.5 : to.getBlockZ() - 0.5);

        e.setTo(to);

        MapNodes.getCurrentGame().getPlayer(e.getPlayer()).getPlayerTasks().add(SchedulerUtil.runSync(() -> {
            FunEffectsUtil.playSound(e.getPlayer(), Sound.ENDERMAN_TELEPORT);
            FunEffectsUtil.playEffect(e.getPlayer(), Effect.ENDER_SIGNAL);
        }, 1));
    }

    @EventHandler
    public void onAchievement(PlayerAchievementAwardedEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPing(ServerListPingEvent e) {
        GameManager gm = MapNodes.getCurrentGame();
        MatchManager mm = NodeFactory.get().getCurrentGame().getMatch();

        e.setNumPlayers((int) gm.getPlayers().filter(GamePlayer::isPlaying).count());
        e.setMaxPlayers(gm.getMaxPlayers());

        e.setMotd(MessageUtil.message(
            "%s%s &7| &5&o%s &7%s \n&f%s",
            gm.getStage().getStageColor(),
            gm.getStage(),
            gm.getMap().getName(),
            Common.formatSeperators(gm.getMap().getVersion(), ChatColor.GRAY, ChatColor.DARK_GRAY),
            ((NodeMap) gm.getMap()).getShortDescription(45)
        ));

        if (mm.getIcon() != null) {
            e.setServerIcon(mm.getIcon());
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Location loc = e.getTo().clone();
        loc.setYaw(0);
        loc.setPitch(0);

        player.setCompassTarget(loc);
    }
}
