package net.year4000.mapnodes.gamemodes.deathmatch;

import com.google.common.base.Joiner;
import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.events.game.GameClockEvent;
import net.year4000.mapnodes.api.events.game.GameLoadEvent;
import net.year4000.mapnodes.api.events.game.GameStartEvent;
import net.year4000.mapnodes.api.events.game.GameWinEvent;
import net.year4000.mapnodes.api.events.team.GameTeamWinEvent;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.GameTeam;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.game.NodeGame;
import net.year4000.mapnodes.game.NodePlayer;
import net.year4000.mapnodes.game.NodeTeam;
import net.year4000.mapnodes.game.regions.types.Point;
import net.year4000.mapnodes.gamemodes.GameModeTemplate;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Common;
import net.year4000.mapnodes.utils.MathUtil;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.mapnodes.utils.TimeUtil;
import net.year4000.utilities.bukkit.FunEffectsUtil;
import net.year4000.utilities.bukkit.bossbar.BossBar;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@GameModeInfo(
    name = "Deathmatch",
    version = "1.0",
    config = DeathmatchConfig.class
)
@EqualsAndHashCode(callSuper = false)
public class Deathmatch extends GameModeTemplate implements GameMode {
    private transient DeathmatchConfig gameModeConfig;
    private transient Map<String, Integer> scores = new HashMap<>();
    private transient NodeGame game;
    private transient GameTeam winner;
    private transient int winnerScore;
    private transient long endTime;

    @EventHandler
    public void onLoad(GameLoadEvent event) {
        gameModeConfig = (DeathmatchConfig) getConfig();
        game = (NodeGame) event.getGame();

        // Add max if map has max score
        if (gameModeConfig.getMaxScore() != null) {
            game.addDynamicGoal("max-score", "&c-- &6MAX &c--", gameModeConfig.getMaxScore());
        }

        game.getPlayingTeams().forEach(team -> {
            scores.put(team.getId(), 0);
            game.addDynamicGoal(team.getId(), team.getId(), team.getDisplayName(), 0);
        });
    }

    @EventHandler
    public void onLoad(GameStartEvent event) {
        if (gameModeConfig.getTimeLimit() != null) {
            endTime = (System.currentTimeMillis() + 1000) + (gameModeConfig.getTimeLimit() * 60000);
        }
    }

    @EventHandler
    public void gameClock(GameClockEvent event) {
        if (!event.getGame().getStage().isPlaying()) return;

        if (gameModeConfig.getTimeLimit() != null) {
            long currentTime = endTime - System.currentTimeMillis();
            String color = Common.chatColorNumber((int) System.currentTimeMillis(), (int) endTime);
            String time = color + (new TimeUtil(currentTime, TimeUnit.MILLISECONDS)).prettyOutput("&7:" + color);

            game.getPlaying().map(GamePlayer::getPlayer).forEach(player -> {
                BossBar.setMessage(player, Msg.locale(player, "deathmatch.clocks.time_left", time), MathUtil.percent((int) Math.abs(endTime - game.getStartTime()), (int) Math.abs(endTime - System.currentTimeMillis())));
            });

            if (currentTime <= 0L) {
                GameTeamWinEvent win = new GameTeamWinEvent(game, winner);

                if (win.getWinner() == null) {
                    win.setWinnerText(Joiner.on("&7, ").join(game.getPlayingTeams().map(NodeTeam::getDisplayName).collect(Collectors.toList())));
                }

                win.call();
            }
        }
    }

    @EventHandler
    public void onGameEnd(GameWinEvent event) {
        scores.forEach((team, score) -> {
            event.getMessage().add(game.getTeams().get(team).getDisplayName() + "&7: " + Common.colorNumber(score, winnerScore));
        });
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        GamePlayer player = game.getPlayer(event.getEntity());

        if (event.getEntity().getKiller() != null) {
            GamePlayer killer = game.getPlayer(event.getEntity().getKiller());

            if (!player.getTeam().getName().equals(killer.getTeam().getName())) {
                addPoint(game, killer.getTeam());
                FunEffectsUtil.playSound(killer.getPlayer(), Sound.FIREWORK_LAUNCH);
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (gameModeConfig.getPointBoxes().size() == 0 || !game.getStage().isPlaying()) return;

        Vector v = event.getTo().toVector();

        // Skip check if not a new block
        if (event.getFrom().toVector().toBlockVector().equals(v.toBlockVector())) return;

        Point point = new Point(v.getBlockX(), v.getBlockY(), v.getBlockZ());
        GamePlayer player = game.getPlayer(event.getPlayer());

        if (!player.isPlaying()) return;

        // Add team point
        game.getRegions().values().forEach(region -> {
            gameModeConfig.getPointBoxes().stream()
                .filter(pointRegion -> pointRegion.getChallenger().equals(((NodeTeam) player.getTeam()).getId()))
                .filter(pointRegion -> pointRegion.getRegion().equals(region.getId()))
                .forEach(pointRegion -> {
                    if (region.inZone(point)) {
                        addPoint(game, player.getTeam(), pointRegion.getPoint());
                        event.setTo(((NodeTeam) player.getTeam()).getSpawns().getSafeRandomSpawn());

                        game.getPlaying().forEach(p -> {
                            p.sendMessage(Msg.locale(p, "deathmatch.scored", player.getPlayerColor(), String.valueOf(pointRegion.getPoint()), ((NodeTeam) player.getTeam()).getDisplayName()));
                        });
                    }
                });
        });
    }

    /** Add one point to the team */
    public void addPoint(NodeGame game, GameTeam team) {
        addPoint(game, team, gameModeConfig.getKillPoint());
    }

    /** Add the amount of points to a team and set the winner */
    public void addPoint(NodeGame game, GameTeam team, int amount) {
        int newScore = scores.get(((NodeTeam) team).getId()) + amount;
        scores.put(((NodeTeam) team).getId(), newScore);
        game.getSidebarGoals().get(((NodeTeam) team).getId()).setScore(scores.get(((NodeTeam) team).getId()));
        game.getPlaying().forEach(p -> (game.getScoreboardFactory()).setGameSidebar((NodePlayer) p));

        // If game team new score is higher than all set as winner
        if (newScore > scores.values().stream().sorted().collect(Collectors.toList()).get(0)) {
            winner = team;
            winnerScore = newScore;

            // If max score is set check to see if the game should end
            if (gameModeConfig.getMaxScore() != null) {
                if (winnerScore >= gameModeConfig.getMaxScore()) {
                    SchedulerUtil.runSync(() -> {
                        if (game.getStage().isPlaying()) {
                            new GameTeamWinEvent(game, winner).call();
                        }
                    }, 5L);
                }
            }
        }
    }
}
