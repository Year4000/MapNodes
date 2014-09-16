package net.year4000.mapnodes.game;

import lombok.Data;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.events.player.GamePlayerJoinEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerJoinSpectatorEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerJoinTeamEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerStartEvent;
import net.year4000.mapnodes.api.game.GameMap;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.GameTeam;
import net.year4000.mapnodes.clocks.Clocker;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.MathUtil;
import net.year4000.mapnodes.utils.Common;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.utilities.bukkit.FunEffectsUtil;
import net.year4000.utilities.bukkit.MessageUtil;
import net.year4000.utilities.bukkit.bossbar.BossBar;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static net.year4000.mapnodes.utils.MathUtil.percent;
import static net.year4000.mapnodes.utils.MathUtil.ticks;

@Data
public final class NodePlayer implements GamePlayer {
    // internals
    private Player player;
    private NodeTeam team;
    private List<BukkitTask> playerTasks = new ArrayList<>();

    // scoreboard things
    private Scoreboard scoreboard;
    private Map<String, Team> teams = new ConcurrentHashMap<>();

    // player flags (set by methods bellow)
    private boolean spectator;
    private boolean playing;
    private boolean entering;

    /** Constructs a game player */
    public NodePlayer(Player player) {
        this.player = player;

        // scoreboard init
        scoreboard = ScoreboardFactory.manager.getNewScoreboard();

        // TODO Better scoreboards
        /*playerTasks.add(SchedulerUtil.runAsync(() -> {
            MapNodes.getCurrentGame().getTeams().values().parallelStream().forEach(team -> {
                Team sbTeam = scoreboard.registerNewTeam(team.getName());
                sbTeam.setAllowFriendlyFire(team.isAllowFriendlyFire());
                sbTeam.setCanSeeFriendlyInvisibles(team.isCanSeeFriendlyInvisibles());
                sbTeam.setDisplayName(team.getName());
                sbTeam.setPrefix(team.getColor().toString());
                sbTeam.setSuffix(MessageUtil.replaceColors("&r"));
                teams.put(team.getName(), sbTeam);
            });
        }));*/
    }

    public void start() {
        playing = true;
        entering = false;

        GamePlayerStartEvent start = new GamePlayerStartEvent(this) {{
            this.setImmortal(true);
            this.setKit(team.getKit());
            this.setTeam(team);
            // todo change to set<spawns>, and spawn type RANDOM, LINEAR
            this.setSpawn(team.getSpawns().get(0)); // TODO Add logic to spawning ex random, linear
            this.setMessage(new ArrayList<String>() {{
                add("&7&m****&a&l Game Started &7&m****"); // TODO proper start message
            }});
        }};
        start.call();

        // team start
        ((NodeTeam) start.getTeam()).start(this);

        // team kit
        ((NodeKit) start.getTeam().getKit()).giveKit(this);

        // spawn tp
        player.teleport(start.getSpawn());

        // God buffer mode
        if (start.isImmortal()) {
            playerTasks.addAll(NodeKit.immortal(player));
        }

        // game start message
        if (start.getMessage() != null) {
            start.getMessage().forEach(this::sendMessage);
        }
    }

    public void join() {
        playing = false;

        joinTeam(null);

        // scoreboard
        player.setScoreboard(scoreboard);

        GamePlayerJoinEvent join = new GamePlayerJoinEvent(this) {{
            this.setSpawn(MapNodes.getCurrentWorld().getSpawnLocation());
            this.setMenu(!MapNodes.getCurrentGame().getStage().isEndGame());
        }};
        join.call();

        // run a tick later to allow player to login
        playerTasks.add(SchedulerUtil.runAsync(() -> {
            player.teleport(join.getSpawn());
            player.setBedSpawnLocation(join.getSpawn(), true);

            // start menu
            if (join.isMenu()) {
                // TODO open inventory
            }
        }, 20L));
    }

    public void leave() {
        playing = false;

        if (team != null) {
            team.leave(this);
        }

        // Cancel tasks
        playerTasks.stream().forEach(BukkitTask::cancel);
        BossBar.removeBar(player);
        //NodeKit.reset(player);
    }

    public void joinTeam(GameTeam gameTeam) {
        // Init team join spectator
        if (team == null || gameTeam == null) {
            spectator = true;
            entering = false;

            // Join
            GameTeam spectatorTeam = MapNodes.getCurrentGame().getTeams().get("spectator");
            GamePlayerJoinSpectatorEvent joinSpectator = new GamePlayerJoinSpectatorEvent(this, spectatorTeam) {{
                this.setDisplay(false);
                this.setKit(spectatorTeam.getKit());
            }};
            joinSpectator.call();
            team = (NodeTeam)joinSpectator.getSpectator();
            team.join(this, joinSpectator.isDisplay());
            team.start(this);

            // Kit
            ((NodeKit) joinSpectator.getKit()).giveKit(this);
        }
        // join new team
        else {
            GamePlayerJoinTeamEvent joinTeam = new GamePlayerJoinTeamEvent(this, team) {{
                this.setTo(gameTeam);
                this.setJoining(MapNodes.getCurrentGame().getStage().isPlaying());
                this.setDisplay(true);
            }};
            joinTeam.call();

            // if event is canceled set player back to spectator
            if (joinTeam.isCancelled()) {
                joinTeam(null);
                return;
            }

            leave();

            spectator = false;
            entering = true;
            team = (NodeTeam) joinTeam.getTo();
            team.join(this, joinTeam.isDisplay());

            if (joinTeam.isJoining()) {
                GamePlayer gamePlayer = this;

                Clocker join = new Clocker(MathUtil.ticks(10)) {
                    private Integer[] ticks = {
                        ticks(5),
                        ticks(4),
                        ticks(3),
                        ticks(2),
                        ticks(1)
                    };

                    public void runFirst(int position) {
                        FunEffectsUtil.playSound(player, Sound.ORB_PICKUP);
                    }

                    public void runTock(int position) {
                        GameMap map = MapNodes.getCurrentGame().getMap();

                        if (Arrays.asList(ticks).contains(position)) {
                            FunEffectsUtil.playSound(player, Sound.NOTE_PLING);
                        }

                        BossBar.setMessage(
                            player,
                            Msg.locale(player, "clocks.join.tock", map.getName(), Common.colorNumber(sec(position), sec(getTime()))),
                            percent(getTime(), position)
                        );
                    }

                    public void runLast(int position) {
                        FunEffectsUtil.playSound(player, Sound.NOTE_BASS);
                        BossBar.setMessage(player, Msg.locale(player, "clocks.join.last"), 1);
                        ((NodePlayer) gamePlayer).start();
                    }
                };
                playerTasks.addAll(join.run());
            }
        }
    }

    /** Send a message with out grabing the player's instance first */
    public void sendMessage(String message, Object... args) {
        player.sendMessage(MessageUtil.message(message, args));
    }

    /** Get the player's color according to the team */
    public String getPlayerColor() {
        return MessageUtil.replaceColors(team.getColor() + player.getName());
    }
}
