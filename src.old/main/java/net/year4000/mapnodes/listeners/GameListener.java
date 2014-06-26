package net.year4000.mapnodes.listeners;

import com.ewized.utilities.bukkit.util.FunEffectsUtil;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.configs.Messages;
import net.year4000.mapnodes.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

@SuppressWarnings("unused")
/** Controls the aspects of what is going on during the game. */
public class GameListener implements Listener {
    /** Register its self. */
    public GameListener() {
        Bukkit.getPluginManager().registerEvents(this, MapNodesPlugin.getInst());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        GameManager gm = WorldManager.get().getCurrentGame();

        GamePlayer gPlayer = gm.getPlayer(event.getPlayer());

        if (gPlayer.getLives() > 0)
            gPlayer.getPlayer().sendMessage(String.format(Messages.get(gPlayer.getPlayer().getLocale(), "game-life"), gPlayer.getLives()));

        event.setRespawnLocation(gPlayer.getTeam().getSafeRandomSpawn());
        gPlayer.respawn();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent event) {
        GameManager gm = WorldManager.get().getCurrentGame();

        if (gm.getStage() != GameStage.PLAYING) return;

        GamePlayer gPlayer = gm.getPlayer(event.getEntity());
        gPlayer.removeScore();
        gPlayer.removeLife();

        // Elimination settings
        if (gm.getMap().isElimination()) {
            // Broadcast elimination
            gm.getPlayers().values().parallelStream().forEach(player -> {
                player.getPlayer().sendMessage(String.format(
                    Messages.get(player.getPlayer().getLocale(), "game-elimination"),
                    gPlayer.getPlayerColor()
                ));
            });
        }

        // Game scores
        if (event.getEntity().getKiller() != null) {
            GamePlayer killer = gm.getPlayer(event.getEntity().getKiller());
            FunEffectsUtil.playEffect(gPlayer.getPlayer(), Effect.FIREWORKS_SPARK);
            FunEffectsUtil.playEffect(gPlayer.getPlayer(), Effect.EXPLOSION);

            if (gPlayer.getTeam() != killer.getTeam()) {
                // Spectator Bonus
                if (gPlayer.isSpecatator()) {
                    killer.addScore(10);
                    FunEffectsUtil.playSound(killer.getPlayer(), Sound.FIREWORK_BLAST);
                }
                // Normal Points
                else if (!killer.isSpecatator()) {
                    killer.getTeam().addScore();
                    killer.addScore();
                    FunEffectsUtil.playSound(killer.getPlayer(), Sound.FIREWORK_LAUNCH);
                }
            }
        }

        // If player ran out of lives
        if (gPlayer.getLives() == 0) {
            gPlayer.getPlayer().sendMessage(Messages.get(gPlayer.getPlayer().getLocale(), "game-life-dead"));

            Bukkit.getScheduler().runTask(MapNodesPlugin.getInst(), () -> {
                gPlayer.respawnPlayer();
                gPlayer.leave();
                GamePlayer.join(event.getEntity());
            });
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent event) {
        // If not a player don't check
        if (!(event.getEntity() instanceof Player)) return;

        GameManager gm = WorldManager.get().getCurrentGame();
        GamePlayer gPlayer = gm.getPlayer((Player)event.getEntity());

        if (!(gPlayer.isSpecatator() || !GameStage.isPlaying() || !gPlayer.isHasPlayed())) {
            // If the damage is void reset player
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                EntityDamageEvent death = new EntityDamageEvent(event.getEntity(), EntityDamageEvent.DamageCause.VOID, 0);
                event.getEntity().setLastDamageCause(death);
                ((Player)event.getEntity()).setHealth(0);
                event.setCancelled(true);
            }
        }
    }
}
