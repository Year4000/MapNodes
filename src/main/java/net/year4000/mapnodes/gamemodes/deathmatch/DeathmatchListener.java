package net.year4000.mapnodes.gamemodes.deathmatch;

import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.events.game.GameClockEvent;
import net.year4000.mapnodes.api.events.game.GameLoadEvent;
import net.year4000.mapnodes.api.events.game.GameStartEvent;
import net.year4000.mapnodes.api.events.team.GameTeamWinEvent;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.GameTeam;
import net.year4000.mapnodes.game.NodeGame;
import net.year4000.mapnodes.game.NodeModeFactory;
import net.year4000.mapnodes.game.NodePlayer;
import net.year4000.mapnodes.game.NodeTeam;
import net.year4000.mapnodes.utils.MathUtil;
import net.year4000.utilities.bukkit.MessageUtil;
import net.year4000.utilities.bukkit.bossbar.BossBar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.joda.time.DateTime;

public class DeathmatchListener implements Listener {
    private Deathmatch gameMode = (Deathmatch) NodeModeFactory.get().getGameMode(Deathmatch.class);
    private DeathmatchConfig gameModeConfig = (DeathmatchConfig) gameMode.getConfig();
    private GameTeam winner;

    @EventHandler
    public void onLoad(GameLoadEvent event) {
        gameMode.setUpGoals();
        event.getGame().addStartControl(() -> true);
    }

    @EventHandler
    public void onLoad(GameStartEvent event) {
        gameMode.setStartTime(System.currentTimeMillis());
        gameMode.setEndTime((System.currentTimeMillis() + 1000) + (gameModeConfig.getTimeLimit() * 60000));
    }

    @EventHandler
    public void gameClock(GameClockEvent event) {
        DateTime display = new DateTime(gameMode.getEndTime()).minus(System.currentTimeMillis());

        event.getGame().getPlaying().map(GamePlayer::getPlayer).forEach(player -> {
            BossBar.setMessage(player, MessageUtil.message("&bTime Left&7: &a%s&7:&a%s", display.toString("mm"), display.toString("ss")), MathUtil.percent((int) Math.abs(gameMode.getEndTime() - gameMode.getStartTime()), (int) Math.abs(gameMode.getEndTime() - System.currentTimeMillis())));
        });

        if ((display.toString("mm") + display.toString("ss")).equals("0000")) {
            new GameTeamWinEvent(winner) {{ game = MapNodes.getCurrentGame(); }}.call();
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
