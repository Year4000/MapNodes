package net.year4000.mapnodes.gamemodes.deathmatch;

import com.google.common.base.Joiner;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.events.game.GameClockEvent;
import net.year4000.mapnodes.api.events.game.GameLoadEvent;
import net.year4000.mapnodes.api.events.game.GameStartEvent;
import net.year4000.mapnodes.api.events.team.GameTeamWinEvent;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.GameTeam;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.game.NodeGame;
import net.year4000.mapnodes.game.NodePlayer;
import net.year4000.mapnodes.game.NodeTeam;
import net.year4000.mapnodes.gamemodes.GameModeTemplate;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.MathUtil;
import net.year4000.utilities.bukkit.FunEffectsUtil;
import net.year4000.utilities.bukkit.MessageUtil;
import net.year4000.utilities.bukkit.bossbar.BossBar;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@GameModeInfo(
    name = "Deathmatch",
    version = "1.0",
    config = DeathmatchConfig.class
)
@Data
@EqualsAndHashCode(callSuper = true)
public class Deathmatch extends GameModeTemplate implements GameMode {
    private DeathmatchConfig gameModeConfig;
    private Map<String, Integer> scores = new HashMap<>();
    private GameTeam winner;
    private long startTime;
    private long endTime;

    @EventHandler
    public void onLoad(GameLoadEvent event) {
        gameModeConfig = (DeathmatchConfig) getConfig();
        ((NodeGame) event.getGame()).getPlayingTeams().forEach(team -> {
            scores.put(team.getId(), 0);
            event.getGame().addDynamicGoal(team.getId(), team.getDisplayName(), 0);
        });
    }

    @EventHandler
    public void onLoad(GameStartEvent event) {
        setStartTime(System.currentTimeMillis());
        setEndTime((System.currentTimeMillis() + 1000) + (gameModeConfig.getTimeLimit() * 60000));
    }

    @EventHandler
    public void gameClock(GameClockEvent event) {
        DateTime display = new DateTime(getEndTime()).minus(System.currentTimeMillis());

        event.getGame().getPlaying().map(GamePlayer::getPlayer).forEach(player -> {
            BossBar.setMessage(player, Msg.locale(player, "clock.time_left", display.toString("mm"), display.toString("ss")), MathUtil.percent((int) Math.abs(getEndTime() - getStartTime()), (int) Math.abs(getEndTime() - System.currentTimeMillis())));
        });

        if ((display.toString("mm") + display.toString("ss")).equals("0000")) {
            new GameTeamWinEvent(event.getGame(), winner) {{
                if (winner == null) {
                    winnerText = Joiner.on(", ").join(((NodeGame) event.getGame()).getPlayingTeams().map(NodeTeam::getDisplayName).collect(Collectors.toList()));
                }
            }}.call();
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        GamePlayer player = MapNodes.getCurrentGame().getPlayer(event.getEntity());

        if (event.getEntity().getKiller() != null) {
            GamePlayer killer = MapNodes.getCurrentGame().getPlayer(event.getEntity().getKiller());

            if (!player.getTeam().getName().equals(killer.getTeam().getName())) {
                addPoint((NodeGame) MapNodes.getCurrentGame(), killer.getTeam());
                FunEffectsUtil.playSound(killer.getPlayer(), Sound.FIREWORK_LAUNCH);
            }
        }
    }

    /** Add a point to a team and set the winner */
    public void addPoint(NodeGame game, GameTeam team) {
        int newScore = scores.get(((NodeTeam) team).getId()) + 1;
        scores.put(((NodeTeam) team).getId(), newScore);
        game.getSidebarGoals().get(((NodeTeam) team).getId()).setScore(scores.get(((NodeTeam) team).getId()));
        game.getPlaying().forEach(p -> (game.getScoreboardFactory()).setGameSidebar((NodePlayer) p));

        // If game team new score is higher than all set as winner
        if (newScore > scores.values().stream().sorted().collect(Collectors.toList()).get(0)) {
            winner = team;
        }
    }
}
