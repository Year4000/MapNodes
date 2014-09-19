package net.year4000.mapnodes.game;

import lombok.AllArgsConstructor;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.utilities.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
public class ScoreboardFactory {
    private static final ScoreboardManager manager = Bukkit.getScoreboardManager();
    private final NodeGame game;
    private final Map<NodePlayer, NodeTeam> playersCurrentState = new ConcurrentHashMap<>();

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
        });

        // Set player to know about other players
        if (!playersCurrentState.containsKey(nodePlayer)) {
            SchedulerUtil.runSync(() -> playersCurrentState.forEach((gamePlayer, gameTeam) -> {
                if (gamePlayer != nodePlayer) {
                    nodePlayer.getScoreboard().getTeam(gameTeam.getName()).addPlayer(gamePlayer.getPlayer());
                }
            }), 10L);
        }

        playersCurrentState.put(nodePlayer, nodeTeam);
    }

    /** Remove player's instance to help with ghost players */
    public void purgeScoreboard(NodePlayer player) {
        playersCurrentState.remove(player);
    }
}
