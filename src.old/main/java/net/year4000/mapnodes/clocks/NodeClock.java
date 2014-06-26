package net.year4000.mapnodes.clocks;

import com.ewized.utilities.bukkit.util.MessageUtil;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.configs.Messages;
import net.year4000.mapnodes.game.GameManager;
import net.year4000.mapnodes.game.GameStage;
import net.year4000.mapnodes.game.GameTeam;
import net.year4000.mapnodes.world.WorldManager;
import org.bukkit.Bukkit;
import org.joda.time.DateTime;

import java.util.Arrays;

public class NodeClock implements Runnable {

    public NodeClock() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(MapNodesPlugin.getInst(), this, 20L, 20L);
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
            DateTime display;
            String color;

            // Normal game play
            if (!gm.getMap().isElimination()) {
                display = new DateTime(gm.getStopTime()).minus(System.currentTimeMillis());
                color = getColor(Integer.valueOf(display.toString("mm")));

                // Should we end the game.
                boolean end = System.currentTimeMillis() - gm.getStopTime() >= 0;
                if (end || gm.shouldEnd()) {
                    gm.stopMatch();
                    return;
                }
            }
            // Elimination mode temp until objectives are in
            else {
                display = new DateTime(System.currentTimeMillis()).minus(gm.getStartTime());
                color = "&a";

                // If the team is eliminated do scoreboard strikeout
                gm.getTeams().values().parallelStream().forEach(team -> {
                    if (team.getCurrentSize() == 0) {
                        gm.getScoreboard().getScoreboard().resetScores(team.getDisplayName());
                        gm.getScoreboard().getSidebarScore(MessageUtil.replaceColors(
                            "&" + team.getChatColor().getChar() +
                            "&m" +
                            team.getName()
                        )).setScore(-1);
                    }
                });

                if ((gm.shouldEndLastTeam() /*&& !gm.isManStart()*/) || gm.getGameSize() == 0) {
                    gm.stopMatch();
                    return;
                }
            }

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
            Arrays.asList(Bukkit.getOnlinePlayers()).parallelStream().forEach(player -> {
                player.kickPlayer(Messages.get(player.getLocale(), "clock-restart-kick"));
            });
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
