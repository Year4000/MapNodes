package net.year4000.mapnodes.gamemodes.deathmatch;

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
import net.year4000.utilities.bukkit.MessageUtil;
import net.year4000.utilities.bukkit.bossbar.BossBar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.joda.time.DateTime;

@GameModeInfo(
    name = "Deathmatch",
    version = "1.0",
    config = DeathmatchConfig.class
)
@Data
@EqualsAndHashCode(callSuper = true)
public class Deathmatch extends GameModeTemplate implements GameMode {
    private DeathmatchConfig gameModeConfig;
    private GameTeam winner;
    private long startTime;
    private long endTime;

    @EventHandler
    public void onLoad(GameLoadEvent event) {
        gameModeConfig = (DeathmatchConfig) getConfig();
        ((NodeGame) MapNodes.getCurrentGame()).getPlayingTeams().forEach(team -> MapNodes.getCurrentGame().addDynamicGoal(team.getId(), team.getDisplayName(), 0));
        event.getGame().addStartControl(() -> true);
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
            new GameTeamWinEvent(MapNodes.getCurrentGame(), winner).call();
        }
    }

    @EventHandler
    public void gameWin(GameTeamWinEvent event) {
        event.getGame().getPlayers().forEach(p -> p.sendMessage("Winner %s !", event.getWinner().getName()));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        GamePlayer player = MapNodes.getCurrentGame().getPlayer(event.getEntity());

        winner = player.getTeam();

        ((NodeGame) MapNodes.getCurrentGame()).getSidebarGoals().get(((NodeTeam) player.getTeam()).getId()).setScore(((NodeGame) MapNodes.getCurrentGame()).getSidebarGoals().get(((NodeTeam) player.getTeam()).getId()).getScore() + 1);
        MapNodes.getCurrentGame().getPlaying().forEach(p -> (((NodeGame) MapNodes.getCurrentGame()).getScoreboardFactory()).setGameSidebar((NodePlayer) p));
    }
}
