package net.year4000.mapnodes.game.scoreboard;

import com.google.common.base.Ascii;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import lombok.AllArgsConstructor;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.game.NodeGame;
import net.year4000.mapnodes.game.NodePlayer;
import net.year4000.mapnodes.game.NodeTeam;
import net.year4000.mapnodes.game.system.Spectator;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Common;
import net.year4000.mapnodes.utils.PacketHacks;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.mapnodes.utils.TimeUtil;
import net.year4000.utilities.ChatColor;
import net.year4000.utilities.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.Iterator;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@AllArgsConstructor
public class ScoreboardFactory {
    private static final ScoreboardManager manager = Bukkit.getScoreboardManager();
    private transient final Map<Scoreboard, List<Team>> tabListTeamNames = new HashMap<>();
    private final NodeGame game;

    // Fancy Title
    private static final Set<String> shimmer = ImmutableSet.of("3", "b", "8", "7", "2", "a", "4", "c", "5", "d", "6", "e", "1", "9");
    private static final String NAME = "Year4000";
    private static final Iterable<String> forever = Iterables.cycle(shimmer);
    private static final Iterator<String> color = forever.iterator();

    static {
        SchedulerUtil.repeatAsync(() -> {
            String b = "&" + color.next() + "&l";
            String name = b + "   [&" + color.next() + "&l" + NAME + b + "]   ";

            Stream.concat(MapNodes.getCurrentGame().getSpectating(), MapNodes.getCurrentGame().getEntering())
                .filter(p -> !((NodePlayer) p).getGame().getStage().isEndGame())
                .map(GamePlayer::getPlayer)
                .map(Player::getScoreboard)
                .map(obj -> obj.getObjective(DisplaySlot.SIDEBAR))
                .filter(obj -> obj != null)
                .forEach(obj -> obj.setDisplayName(MessageUtil.replaceColors(name)));
        }, 20L);
    }

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

    /** Update player's custom display tab list name */
    public void setOrUpdateListName(NodePlayer viewer, NodePlayer player) {
        // The sorting algorithm
        int sortedHash = player.getTeam() instanceof Spectator ? Integer.MAX_VALUE : player.getTeam().getId().hashCode();
        sortedHash /= player.getBadgeRank();
        char[] charName = player.getPlayer().getName().toCharArray();

        for (int i = 0; i < player.getSplitName()[0].length(); i++) {
            sortedHash -= Ascii.toLowerCase(charName[i]);
        }

        sortedHash = Math.abs(sortedHash);
        String hash = "tab:" + String.valueOf(sortedHash);

        // set how the display looks
        String color = player.getTeam().getColor().toString();
        color = viewer == player ? Common.fcolor(ChatColor.ITALIC, color) : color;
        String prefix = MessageUtil.replaceColors(player.getBadge() + " " + color);
        player.getPlayer().setPlayerListName(player.getSplitName()[0]);
        Scoreboard scoreboard = viewer.getScoreboard();

        // Create record in Map
        if (!tabListTeamNames.containsKey(scoreboard)) {
            tabListTeamNames.put(scoreboard, new ArrayList<>());
        }

        if (scoreboard.getTeam(hash) == null) {
            // Purge name from old teams
            tabListTeamNames.get(scoreboard).forEach(team -> team.remove(player.getSplitName()[0]));

            // Create new team and assign player to it
            Team team = scoreboard.registerNewTeam(hash);
            tabListTeamNames.get(scoreboard).add(team);
            team.setPrefix(prefix);
            team.setSuffix(player.getSplitName()[1]);
            team.add(player.getSplitName()[0]);
        }
        else {
            Team team = scoreboard.getTeam(hash);
            team.setPrefix(prefix);
            team.add(player.getSplitName()[0]);
        }
    }

    /** Set the team of the player for the player's personal scoreboard */
    public void setTeam(NodePlayer nodePlayer, NodeTeam nodeTeam) {
        // Set all other players to know about the player
        game.getPlayers().map(player -> (NodePlayer) player).forEach(player -> {
            // Remove Player
            player.getScoreboard().getTeams().stream()
                .forEach(team -> team.removePlayer(nodePlayer.getPlayer()));

            // Add Player
            player.getScoreboard().getTeams().stream()
                .filter(team -> team.getName().equals(nodeTeam.getName()))
                .forEach(team -> team.addPlayer(nodePlayer.getPlayer()));

            nodePlayer.getScoreboard().getTeam(player.getTeam().getName()).addPlayer(player.getPlayer());

            setOrUpdateListName(player, nodePlayer);
            setOrUpdateListName(nodePlayer, player);
        });
    }

    public void setAllPersonalSidebar() {
        Stream.concat(game.getSpectating(), game.getEntering())
            .map(player -> (NodePlayer) player)
            .forEach(this::setPersonalSidebar);
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
        boolean queue;

        if (nodePlayer.getPendingTeam() != null) {
            queue = nodePlayer.getPendingTeam().getQueue().contains(nodePlayer);
        }
        else {
            queue = nodePlayer.getTeam().getQueue().contains(nodePlayer);
        }

        SidebarManager side = new SidebarManager();

        // When game is running show game time length
        if (game.getStage().isPlaying() && !PacketHacks.isTitleAble(nodePlayer.getPlayer())) {
            long currentTime = System.currentTimeMillis() - game.getStartTime();
            String time = "&a" + (new TimeUtil(currentTime, TimeUnit.MILLISECONDS)).prettyOutput("&7:&a");
            side.addLine(Msg.locale(nodePlayer, "game.time", time));
            side.addBlank();
        }

        // Team Selection
        side.addLine(Msg.locale(nodePlayer, "team.name"));
        String name;
        if (nodePlayer.getPendingTeam() != null) {
            name = " " + nodePlayer.getPendingTeam().getDisplayName();
        }
        else {
            name = " " + nodePlayer.getTeam().getDisplayName();
        }

        if (queue) {
            name = Common.fcolor(ChatColor.BOLD, name);
        }

        side.addLine(name);

        // When the map has classes
        if (game.getClasses().size() > 0) {
            side.addBlank();
            side.addLine(Msg.locale(nodePlayer, "class.name"));
            if (nodePlayer.hasClassKit()) {
                side.addLine(" &a" + nodePlayer.getClassKit().getName());
            }
            else {
                side.addLine(" " + Msg.locale(nodePlayer, "class.default"));
            }
        }

        side.addBlank();
        side.addLine(Msg.locale(nodePlayer, "team.players"));
        game.getTeams().values().stream()
            .sorted((left, right) -> (left instanceof Spectator) ? -1 : 1)
            .forEach(team -> {
                String teamSize = Common.colorCapacity(team.getPlayers().size(), team.getSize());
                String teamName = team.getDisplayName();

                // If Same team show in italic
                if ((nodePlayer.getPendingTeam() != null && nodePlayer.getPendingTeam().equals(team)) || (nodePlayer.getTeam().equals(team) && nodePlayer.getPendingTeam() == null)) {
                    teamName = Common.fcolor(ChatColor.ITALIC, teamName);
                }

                side.addLine(" &7(" + teamSize + "&7) " + teamName);
            });

        side.buildSidebar(nodePlayer.getScoreboard(), header);
    }

    public void setAllGameSidebar() {
        game.getPlaying()
            .map(player -> (NodePlayer) player)
            .forEach(this::setPersonalSidebar);
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
