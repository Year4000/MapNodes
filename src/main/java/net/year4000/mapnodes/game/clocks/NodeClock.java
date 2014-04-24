package net.year4000.mapnodes.game.clocks;

import com.ewized.utilities.bukkit.util.MessageUtil;
import net.year4000.mapnodes.MapNodes;
import net.year4000.mapnodes.configs.Messages;
import net.year4000.mapnodes.game.GameManager;
import net.year4000.mapnodes.game.GameStage;
import net.year4000.mapnodes.game.GameTeam;
import net.year4000.mapnodes.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.joda.time.DateTime;

public class NodeClock implements Runnable {

    public NodeClock() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(MapNodes.getInst(), this, 20L, 20L);
    }

    @Override
    public void run() {
        GameManager gm = WorldManager.get().getCurrentGame();

        // Clock to run while in the lobby
        if (GameStage.isWaiting()) {
            boolean start = false;
            for (GameTeam team : gm.getTeams().values()) {
                start = team.getCurrentSize() > 0;
                if (!start) break;
            }
            if (start) {
                gm.startMatch();
            }
        }
        // Clock to run during the game.
        else if (GameStage.isPlaying()) {
            DateTime display = new DateTime(gm.getStopTime()).minus(System.currentTimeMillis());

            // Should we end the game.
            boolean end = System.currentTimeMillis() - gm.getStopTime() >= 0;
            if (end || gm.shouldEnd()) {
                gm.stopMatch();
                return;
            }

            String color = getColor(Integer.valueOf(display.toString("mm")));
            String mapName = gm.getMap().getName();

            gm.getScoreboard().getSidebar().setDisplayName(MessageUtil.replaceColors(String.format(
                "&b%s %s%s&7:%s%s",
                // Map name
                (mapName.length() >= 18) ? mapName.substring(0, 17) : mapName,
                // Color format on time
                color,
                // Time
                display.toString("mm"),
                color,
                display.toString("ss")
            )));
        }
        else if (GameStage.isEnded()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.kickPlayer(Messages.get(player.getLocale(), "clock-restart-kick"));
            }
            Bukkit.shutdown();
        }
    }

    /** Get the color for the number */
    private String getColor(int time) {
        String color;

        if (time < 1) color = "&4";
        else if (time < 2) color = "&c";
        else if (time < 3) color = "&6";
        else if (time < 5) color = "&e";
        else color = "&a";

        return color;
    }
}
