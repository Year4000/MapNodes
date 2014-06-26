package net.year4000.mapnodes.addons;

import com.ewized.utilities.bukkit.util.FunEffectsUtil;
import com.ewized.utilities.bukkit.util.MessageUtil;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.game.GameManager;
import net.year4000.mapnodes.game.GamePlayer;
import net.year4000.mapnodes.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Arrays;
import java.util.List;

public class KillStreak implements Listener {
    public KillStreak() {
        Bukkit.getPluginManager().registerEvents(this, MapNodesPlugin.getInst());
    }

    @SuppressWarnings("unused")
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled = true)
    public void onKill(PlayerDeathEvent event) {
        GameManager gm = WorldManager.get().getCurrentGame();
        GamePlayer killie = gm.getPlayer(event.getEntity());

        if (event.getEntity().getKiller() != null) {
            GamePlayer killer = gm.getPlayer(event.getEntity().getKiller());

            boolean multipleKill = Math.abs(killer.getLastKill() - System.currentTimeMillis()) > 5000L;
            if (multipleKill) killer.setQuickKills(0);

            killer.setKillStreak(killer.getKillStreak() + 1);
            killer.setQuickKills(killer.getQuickKills() + 1);

            if (killer.getKillStreak() > 1 && getMessage(killer) != null) {
                killer.getPlayer().sendMessage(getMessage(killer));
                FunEffectsUtil.playEffect(killer.getPlayer(), Effect.GHAST_SHRIEK);
            }

            List<Player> online = Arrays.asList(Bukkit.getOnlinePlayers());

            if (getBroadcast(killer) != null) {
                MessageUtil.broadcast(getBroadcast(killer));
                online.forEach(player -> FunEffectsUtil.playSound(player, Sound.FIREWORK_TWINKLE));
            }

            if (killie.getKillStreak() >= 5 && getDefeat(killer, killie) != null) {
                MessageUtil.broadcast(getDefeat(killer, killie));
                if (killie.getKillStreak() < 15) {
                    online.forEach(player -> FunEffectsUtil.playSound(player, Sound.BLAZE_DEATH));
                }
                else {
                    online.forEach(player -> FunEffectsUtil.playSound(player, Sound.ENDERDRAGON_DEATH));
                }
            }

            killer.setLastKill(System.currentTimeMillis());
        }

        killie.setKillStreak(0);
        killie.setQuickKills(0);
    }

    /** Gets the message to show to the player. */
    private String getMessage(GamePlayer killer) {
        int killStreak = killer.getQuickKills();
        boolean multipleKill = Math.abs(killer.getLastKill() - System.currentTimeMillis()) < 5000L;
        String message = null;

        if ((killStreak == 2) && (multipleKill))
            message = "&7-- &a&lDouble kill!!! &7--";

        else if ((killStreak == 3) && (multipleKill))
            message = "&7-- &a&lMulti kill!!! &7--";

        else if ((killStreak == 4) && (multipleKill))
            message = "&7-- &a&lMega kill!!! &7--";

        else if ((killStreak == 5) && (multipleKill))
            message = "&7-- &a&lUltra kill!!! &7--";

        else if ((killStreak >= 6) && (multipleKill))
            message = "&7-- &a&lMonster kill!!! &7--";

        return message == null ? null : MessageUtil.message(message);
    }

    /** Gets the broadcast message to show to everyone. */
    private String getBroadcast(GamePlayer killer) {
        int killStreak = killer.getKillStreak();
        String message = null;

        if (killStreak == 7)
            message = "is on a killing spree.";

        else if (killStreak == 10)
            message = "is dominating!";

        else if (killStreak == 15)
            message = "is on a rampage!";

        else if (killStreak == 20)
            message = "is unstoppable!";

        else if (killStreak >= 25)
            message = "is on a massacre!";

        return message == null ? null : MessageUtil.message(
            "%s &a&o%s %s",
            killer.getPlayerColor(),
            message,
            "&7&o(&a&o" + killStreak + " kills&7&o)"
        );
    }

    /** Gets the defeat message to show to everyone. */
    private String getDefeat(GamePlayer killer, GamePlayer killie) {
        int killStreak = killie.getKillStreak();
        String message = null;

        if (killStreak >= 25)
            message = "rain of terror";

        else if (killStreak >= 20)
            message = "unstoppable ability";

        else if (killStreak >= 15)
            message = "rampage";

        else if (killStreak >= 10)
            message = "domination";

        else if (killStreak >= 7)
            message = "killing spree";

        return message == null ? null : MessageUtil.message(
            "%s&a&o's %s %s %s&a&o!",
            killie.getPlayerColor(),
            message,
            "was stopped by",
            killer.getPlayerColor()
        );
    }
}