package net.year4000.mapnodes.gamemodes.tntwars;

import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.events.game.GameLoadEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerStartEvent;
import net.year4000.mapnodes.api.events.team.GameTeamWinEvent;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.GameRegion;
import net.year4000.mapnodes.api.game.GameTeam;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.game.NodePlayer;
import net.year4000.mapnodes.game.regions.types.Global;
import net.year4000.mapnodes.game.regions.types.Point;
import net.year4000.mapnodes.gamemodes.GameModeTemplate;
import net.year4000.mapnodes.utils.NMSHacks;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDispenseEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@GameModeInfo(
    name = "TNT Wars",
    version = "1.0",
    config = TntWarsConfig.class
)
public class TntWars extends GameModeTemplate implements GameMode {
    private TntWarsConfig gameModeConfig;
    private Map<String, Integer> scores = new HashMap<>();
    private GameManager game;
    private GameTeam winner;
    private int winnerScore;
    private Map<Integer, Vector> locations = new HashMap<>();
    private List<Integer> fromDispenser = new ArrayList<>();

    // Safe TNT //

    @EventHandler
    public void onLoad(GameLoadEvent event) {
        gameModeConfig = getConfig();
        game = event.getGame();
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
    public void onPrimed(ExplosionPrimeEvent event) {
        locations.put(event.getEntity().getEntityId(), event.getEntity().getLocation().toVector());
    }

    @EventHandler
    public void onPrimed(BlockDispenseEntityEvent event) {
        if (!(event.getEntity() instanceof TNTPrimed)) return;

        locations.put(event.getEntity().getEntityId(), event.getEntity().getLocation().toVector());
        fromDispenser.add(event.getEntity().getEntityId());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPrimed(EntityExplodeEvent event) {
        try {
            if (locations.containsKey(event.getEntity().getEntityId())) {
                Vector current = event.getLocation().toVector();
                Vector old = locations.remove(event.getEntity().getEntityId());

                if (Math.abs(current.getBlockZ() - old.getBlockZ()) < 10 && Math.abs(current.getBlockX() - old.getBlockX()) < 10) {

                    if (fromDispenser.remove((Integer) event.getEntity().getEntityId())) {
                        Block baseBlock = old.toLocation(MapNodes.getCurrentWorld()).getBlock();

                        event.getEntity().getNearbyEntities(1.5, 1.5, 1.5)
                            .stream()
                            .filter(e -> e instanceof TNTPrimed)
                            .forEach(Entity::remove);

                        for (BlockFace face : BlockFace.values()) {
                            Block dispenser = baseBlock.getRelative(face);
                            if (dispenser.getType() == Material.DISPENSER) {
                                dispenser.setType(Material.AIR);
                            }
                        }
                    }

                    event.setCancelled(true);
                }
            }
        }
        catch (NullPointerException e) {
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
        List<GameRegion> regionList = game.getRegions().values().stream()
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

        // Tokens
        LivingEntity livingEntity = NMSHacks.getTNTSource(event.getEntity());

        if (livingEntity instanceof Player) {
            GamePlayer player = MapNodes.getCurrentGame().getPlayer((Player) livingEntity);
            if (player != null) {
                if (MapNodesPlugin.getInst().isDebug()) {
                    String tokens = MessageUtil.replaceColors("&7(DEBUG) &b+2 &6tokens ");
                    player.sendMessage(tokens);
                    MapNodesPlugin.debug(player.getPlayerColor() + " " + tokens);
                    ((NodePlayer) player).getCreditsMultiplier().incrementAndGet();
                }
                else {
                    player.sendMessage(MessageUtil.replaceColors("&b+2 &6tokens"));
                    MapNodesPlugin.getInst().getApi().addTokens(player, 2);
                    ((NodePlayer) player).getCreditsMultiplier().incrementAndGet();
                }
            }
        }
    }

    /** Add the amount of points to a team and set the winner */
    public void addPoint(GameManager game, GameTeam team, int amount) {
        int newScore = scores.get(team.getId()) + amount;
        scores.put(team.getId(), newScore);
        game.getSidebarGoals().get(team.getId()).setScore(scores.get(team.getId()));

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
