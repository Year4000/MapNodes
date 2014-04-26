package net.year4000.mapnodes.listeners;

import com.ewized.utilities.bukkit.util.FunEffectsUtil;
import net.year4000.mapnodes.MapNodes;
import net.year4000.mapnodes.configs.Messages;
import net.year4000.mapnodes.game.GameManager;
import net.year4000.mapnodes.game.GamePlayer;
import net.year4000.mapnodes.game.GameStage;
import net.year4000.mapnodes.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

@SuppressWarnings("unused")
/** Controls the aspects of what is going on during the game. */
public class GameListener implements Listener {
    /** Register its self. */
    public GameListener() {
        Bukkit.getPluginManager().registerEvents(this, MapNodes.getInst());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        GameManager gm = WorldManager.get().getCurrentGame();
        final GamePlayer gPlayer = gm.getPlayer(event.getPlayer());

        // Remove live if bigger than 0
        if (gPlayer.getLives() > 0)
            gPlayer.removeLife();

        // If player ran out of lives
        if (gPlayer.getLives() == 0) {
            if (gm.getMap().isElimination()) {
                for (GamePlayer player : gm.getPlayers().values()) {
                    player.getPlayer().sendMessage(String.format(
                        Messages.get(player.getPlayer().getLocale(), "game-elimination"),
                        gPlayer.getPlayerColor()
                    ));
                }
            }
            else {
                gPlayer.getPlayer().sendMessage(Messages.get(gPlayer.getPlayer().getLocale(), "game-life-dead"));
            }

            // Run later to give time for player to respawn
            Bukkit.getScheduler().runTask(MapNodes.getInst(), () -> {
                gPlayer.leave();
                GamePlayer.join(event.getPlayer());
            });
        }
        // If player sill have lives
        else {
            if (gPlayer.getLives() > 0)
                gPlayer.getPlayer().sendMessage(String.format(Messages.get(gPlayer.getPlayer().getLocale(), "game-life"), gPlayer.getLives()));
            event.setRespawnLocation(gPlayer.getTeam().getSafeRandomSpawn());
            gPlayer.respawn();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent event) {
        GameManager gm = WorldManager.get().getCurrentGame();

        if (gm.getStage() != GameStage.PLAYING) return;

        GamePlayer gPlayer = gm.getPlayer(event.getEntity());
        gPlayer.removeScore();

        if (event.getEntity().getKiller() != null) {
            GamePlayer killer = gm.getPlayer(event.getEntity().getKiller());
            FunEffectsUtil.playEffect(gPlayer.getPlayer(), Effect.SMOKE);

            if (gPlayer.getTeam() != killer.getTeam()) {
                // Spectator Bonus
                if (gPlayer.isSpecatator()) {
                    killer.addScore(10);
                    FunEffectsUtil.playSound(killer.getPlayer(), Sound.FIREWORK_BLAST);
                }
                // Normal Points
                else {
                    killer.getTeam().addScore();
                    killer.addScore();
                    FunEffectsUtil.playSound(killer.getPlayer(), Sound.FIREWORK_LAUNCH);
                }
            }
        }

    }
}
