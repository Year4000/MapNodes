package net.year4000.mapnodes.game;

import net.year4000.mapnodes.api.MapNodes;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public final class ScoreboardFactory {
    public static final ScoreboardManager manager = Bukkit.getScoreboardManager();

    /** Join a player to a team */
    public static void updateTeams() {
        /*MapNodes.getCurrentGame().getPlayers().parallel().forEach(gamePlayer -> {
            MapNodes.getCurrentGame().getTeams().values().forEach(gameTeam -> {
                Team team = ((NodePlayer) gamePlayer).getTeams().get(gameTeam.getName());
                team.getEntries().forEach(team::remove);

                gameTeam.getPlayers().forEach(teamPlayer -> team.add(teamPlayer.getPlayer().getName()));
            });
        });*/
    }

}
