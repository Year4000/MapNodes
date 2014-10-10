package net.year4000.mapnodes.game.scoreboard;

import lombok.AllArgsConstructor;
import net.year4000.mapnodes.game.NodeGame;
import net.year4000.mapnodes.game.NodePlayer;
import net.year4000.mapnodes.game.NodeTeam;
import net.year4000.mapnodes.game.system.Spectator;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Common;
import net.year4000.mapnodes.utils.TimeUtil;
import net.year4000.utilities.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class ScoreboardFactory {
    private static final ScoreboardManager manager = Bukkit.getScoreboardManager();
    private final NodeGame game;

    /** Create a scoreboard for the player */
    public Scoreboard createScoreboard(NodePlayer player) {
        Scoreboard scoreboard = manager.getNewScoreboard();

        // Assign default teams (used for seeing invisible and friendly fire)
        game.getTeams().values().forEach(nodeTeam -> {
            Team team = scoreboard.registerNewTeam(nodeTeam.getName());
            team.setAllowFriendlyFire(nodeTeam.isAllowFriendlyFire());
            team.setCanSeeFriendlyInvisibles(nodeTeam.isCanSeeFriendlyInvisibles());
            team.setPrefix(nodeTeam.getColor().toString());
            team.setSuffix(ChatColor.RESET.toString());
        });

        return scoreboard;
    }

    /** Set the team of the player for the player's personal scoreboard */
    public void setTeam(NodePlayer nodePlayer, NodeTeam nodeTeam) {
        // Set all other players to know about the player
        game.getPlayers().forEach(player -> {
            // Remove Player
            ((NodePlayer) player).getScoreboard().getTeams().stream()
                .forEach(team -> team.removePlayer(nodePlayer.getPlayer()));

            // Add Player
            ((NodePlayer) player).getScoreboard().getTeams().stream()
                .filter(team -> team.getName().equals(nodeTeam.getName()))
                .forEach(team -> team.addPlayer(nodePlayer.getPlayer()));

            nodePlayer.getScoreboard().getTeam(player.getTeam().getName()).addPlayer(player.getPlayer());

        });
    }

    public void setPersonalSidebar(NodePlayer nodePlayer) {
        setPersonalSidebar(nodePlayer, "&3&l   [&b&lYear4000&3&l]   ");
    }

    public void setPersonalSidebar(NodePlayer nodePlayer, String header) {
        // When game has ended only show game sidebar
        if (game.getStage().isEndGame()) {
            setGameSidebar(nodePlayer);
            return;
        }

        NodeGame game = nodePlayer.getGame();
        String queue;
        if (nodePlayer.getPendingTeam() != null) {
            queue = nodePlayer.getPendingTeam().getQueue().contains(nodePlayer) ? Msg.locale(nodePlayer, "team.queue") : "";
        }
        else {
            queue = nodePlayer.getTeam().getQueue().contains(nodePlayer) ? Msg.locale(nodePlayer, "team.queue") : "";
        }

        SidebarManager side = new SidebarManager();

        // When game is running show game time length
        if (game.getStage().isPlaying()) {
            long currentTime = System.currentTimeMillis() - game.getStartTime();
            String time = (new TimeUtil(currentTime, TimeUnit.MILLISECONDS)).prettyOutput();
            side.addLine(Msg.locale(nodePlayer, "game.time", time));
            side.addBlank();
        }

        // Team Selection
        side.addLine(Msg.locale(nodePlayer, "team.name"));
        if (nodePlayer.getPendingTeam() != null) {
            side.addLine(" " + nodePlayer.getPendingTeam().getDisplayName() + " " + queue);
        }
        else {
            side.addLine(" " + nodePlayer.getTeam().getDisplayName() + " " + queue);
        }

        side.addBlank();
        side.addLine(Msg.locale(nodePlayer, "team.players"));
        game.getTeams().values().stream()
            .sorted((left, right) -> (left instanceof Spectator) ? -1 : 1)
            .forEach(team -> {
                String teamSize = Common.colorCapacity(team.getPlayers().size(), team.getSize());
                side.addLine(" " + teamSize + " " + team.getDisplayName());
            });


        // When the map has classes
        if (game.getClasses().size() > 0) {
            side.addLine(Msg.locale(nodePlayer, "class.name"));
            //side.addLine("  " + nodePlayer.getClazz().getDisplayName());
        }

        side.buildSidebar(nodePlayer.getScoreboard(), header);
    }

    public void setGameSidebar(NodePlayer nodePlayer) {
        SidebarManager side = new SidebarManager();

        nodePlayer.getGame().getSidebarGoals().values().forEach(goal -> {
            if (goal.getType() == SidebarGoal.GoalType.DYNAMIC) {
                side.addLine(Msg.locale(nodePlayer, goal.getTeamDisplay(nodePlayer)), goal.getScore());
            }
            else if (goal.getType() == SidebarGoal.GoalType.STATIC) {
                if (goal.getDisplay().equals("")) {
                    side.addBlank();
                }
                else {
                    side.addLine(Msg.locale(nodePlayer, goal.getTeamDisplay(nodePlayer)));
                }
            }
            else {
                throw new UnsupportedOperationException(goal.getType().name() + " is not a valid goal type.");
            }
        });

        // 22 is 32 - 10 the number comes from the padding of the map name
        String shortMapName = Common.shortMessage(22, nodePlayer.getGame().getMap().getName());
        side.buildSidebar(nodePlayer.getScoreboard(), "    &b" + shortMapName + "    ");
    }
}
