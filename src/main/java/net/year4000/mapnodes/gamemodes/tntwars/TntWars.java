package net.year4000.mapnodes.gamemodes.tntwars;

import com.google.common.base.Joiner;
import net.year4000.mapnodes.api.events.game.GameClockEvent;
import net.year4000.mapnodes.api.events.game.GameLoadEvent;
import net.year4000.mapnodes.api.events.game.GameStartEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerStartEvent;
import net.year4000.mapnodes.api.events.team.GameTeamWinEvent;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.GameTeam;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.game.NodeGame;
import net.year4000.mapnodes.game.NodePlayer;
import net.year4000.mapnodes.game.NodeRegion;
import net.year4000.mapnodes.game.NodeTeam;
import net.year4000.mapnodes.game.regions.types.*;
import net.year4000.mapnodes.gamemodes.GameModeTemplate;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Common;
import net.year4000.mapnodes.utils.MathUtil;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.mapnodes.utils.TimeUtil;
import net.year4000.utilities.bukkit.bossbar.BossBar;
import org.bukkit.Location;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDispenseEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@GameModeInfo(
    name = "TNT Wars",
    version = "1.0",
    config = TntWarsConfig.class
)
public class TntWars extends GameModeTemplate implements GameMode {
    private TntWarsConfig gameModeConfig;
    private Map<String, Integer> scores = new HashMap<>();
    private NodeGame game;
    private GameTeam winner;
    private int winnerScore;
    private long endTime;

    @EventHandler
    public void onLoad(GameLoadEvent event) {
        gameModeConfig = (TntWarsConfig) getConfig();
        game = (NodeGame) event.getGame();
        game.addStartTime(60);

        // Add max if map has max score
        if (gameModeConfig.getMaxScore() != null) {
            game.addDynamicGoal("max-score", "&c-- &6MAX &c--", gameModeConfig.getMaxScore());
        }

        game.getPlayingTeams().forEach(team -> {
            scores.put(team.getId(), 0);
            game.addDynamicGoal(team.getId(), team.getId(), team.getDisplayName(), 0);
        });
        game.addStaticGoal("", "");
        game.addStaticGoal("islands", "tnt_wars.islands");
        game.getPlayingTeams().forEach(team -> {
            TntWarsConfig.Island island = gameModeConfig.getIsland(team.getId());
            island.initIsland(game);
            game.addStaticGoal(island.getId(), island.getDisplay());
        });
    }

    @EventHandler
    public void onPlayerStart(GamePlayerStartEvent event) {
        event.setImmortal(false);
    }

    @EventHandler
    public void onLoad(GameStartEvent event) {
        if (gameModeConfig.getTimeLimit() != null) {
            endTime = (System.currentTimeMillis() + 1000) + (TimeUnit.MILLISECONDS.convert(gameModeConfig.getTimeLimit().toSecs(), TimeUnit.SECONDS));
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
                BossBar.setMessage(player, Msg.locale(player, "tnt_wars.clocks.time_left", time), MathUtil.percent((int) Math.abs(endTime - game.getStartTime()), (int) Math.abs(endTime - System.currentTimeMillis())));
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

    // Safe TNT //

    private Map<Integer, Vector> locations = new HashMap<>();

    @EventHandler
    public void onPrimed(ExplosionPrimeEvent event) {
        locations.put(event.getEntity().getEntityId(), event.getEntity().getLocation().toVector());
    }

    @EventHandler
    public void onPrimed(BlockDispenseEntityEvent event) {
        if (!(event.getEntity() instanceof TNTPrimed)) return;

        locations.put(event.getEntity().getEntityId(), event.getEntity().getLocation().toVector());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPrimed(EntityExplodeEvent event) {
        try {
            if (locations.containsKey(event.getEntity().getEntityId())) {
                Vector current = event.getLocation().toVector();
                Vector old = locations.remove(event.getEntity().getEntityId());

                if (Math.abs(current.getBlockZ() - old.getBlockZ()) < 10 && Math.abs(current.getBlockX() - old.getBlockX()) < 10) {
                    event.setCancelled(true);
                }
            }
        } catch (NullPointerException e) {
            // Not a proper entity
        }
    }

    // Game Points //

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        GamePlayer player = game.getPlayer(event.getEntity());

        game.getPlayingTeams().forEach(team -> {
            if (team != player.getTeam()) {
                addPoint(game, team, 25);
            }
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onTNT(EntityExplodeEvent event) {
        Point point = new Point(event.getLocation().toVector().toBlockVector());
        List<NodeRegion> regionList = game.getRegions().values().stream()
            .filter(region -> region.getZones().stream().filter(zone -> zone instanceof Global || zone instanceof net.year4000.mapnodes.game.regions.types.Void).count() == 0)
            .filter(region -> region.inZone(point))
            .sorted((r, l) -> r.getWeight() < l.getWeight() ? 1 : -1)
            .collect(Collectors.toList());

        if (regionList.size() == 0) return;

        String regionName = regionList.get(0).getId();
        TntWarsConfig.Island island = gameModeConfig.getIsland(regionName);
        int score = (int) Math.sqrt(event.blockList().size());

        island.updatePercent(score);
        game.getSidebarGoals().get(island.getId()).setDisplay(island.getDisplay());

        game.getPlayingTeams().forEach(team -> {
            if (team != island.getTeam()) {
                addPoint(game, team, score);
            }
        });

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
