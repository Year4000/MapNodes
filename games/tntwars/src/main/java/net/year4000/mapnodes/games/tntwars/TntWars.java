/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.games.tntwars;

import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntity;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.events.game.GameLoadEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerDeathEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerStartEvent;
import net.year4000.mapnodes.api.events.team.GameTeamWinEvent;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.GameRegion;
import net.year4000.mapnodes.api.game.GameTeam;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.backend.MapNodesBadgeManager;
import net.year4000.mapnodes.game.NodePlayer;
import net.year4000.mapnodes.game.regions.types.Global;
import net.year4000.mapnodes.game.regions.types.Point;
import net.year4000.mapnodes.GameModeTemplate;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.NMSHacks;
import net.year4000.mapnodes.utils.PacketHacks;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.utilities.TimeUtil;
import net.year4000.utilities.bukkit.ItemUtil;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;
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
    private GameManager game;
    private GameTeam winner;
    private int winnerScore;
    private Map<Integer, Vector> locations = new HashMap<>();
    private List<Integer> fromDispenser = new ArrayList<>();
    private List<Vector> fromDispensers = Lists.newArrayList();
    private Map<UUID, Long> launchTimes = Maps.newHashMap();

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

        // Register the packet to grab the tnt dispensed from the dispenser
        MapNodes.getProtocolManager().addPacketListener(new PacketAdapter(MapNodesPlugin.getInst(), PacketType.Play.Server.SPAWN_ENTITY) {
            @Override
            public void onPacketSending(PacketEvent event) {
                WrapperPlayServerSpawnEntity entity = new WrapperPlayServerSpawnEntity(event.getPacket());
                Vector vector = new Vector(entity.getX(), entity.getY(), entity.getZ());

                if (fromDispensers.remove(vector)) {
                    locations.putIfAbsent(entity.getEntityID(), vector);
                    fromDispenser.add(entity.getEntityID());
                }
            }
        });
    }

    @EventHandler
    public void onPlayerStart(GamePlayerStartEvent event) {
        event.setImmortal(false);
    }

    @EventHandler
    public void onTNTLaunch(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        NodePlayer nodePlayer = (NodePlayer) MapNodes.getCurrentGame().getPlayer(player);
        long currentTime = System.currentTimeMillis(), playerTime;
        boolean rightAir = event.getAction() == Action.RIGHT_CLICK_AIR;
        boolean rightBlock = event.getAction() == Action.RIGHT_CLICK_BLOCK;
        boolean material = event.getMaterial() == Material.TNT;

        if (rightAir && material && !rightBlock) {
            if ((playerTime = launchTimes.getOrDefault(player.getUniqueId(), currentTime)) > currentTime) {
                String time = "&a" + new TimeUtil(playerTime - currentTime + 1000, TimeUnit.MILLISECONDS).prettyOutput("&7:&a");
                PacketHacks.setTitle(player, "", Msg.locale(player, "tnt_wars.throw.tnt", time), 5, 10, 5);
                return;
            }
            else if (nodePlayer.getCache().getBadge().getRank() == 1) {
                // Let the player know VIP has less wait time
                player.sendMessage(Msg.locale(player, "tnt_wars.throw.vip"));
            }

            Location location = player.getEyeLocation().clone().subtract(0, 0.25, 0);
            Entity tnt = MapNodes.getCurrentWorld().spawnEntity(location, EntityType.PRIMED_TNT);
            double pitch = player.getLocation().getPitch() > 0 ? 90 - player.getLocation().getPitch() : player.getLocation().getPitch() + 270;
            tnt.setVelocity(location.getDirection().normalize().multiply(Math.log(pitch) - 0.25));
            ItemStack tntStack = player.getItemInHand();

            if (tntStack.getAmount() > 1) {
                tntStack.setAmount(tntStack.getAmount() - 1);
            }
            else {
                player.setItemInHand(ItemUtil.makeItem("air"));
            }

            locations.putIfAbsent(tnt.getEntityId(), location.toVector());
            // VIP perk higher you are less time to wait
            int waitTime = 1 + MapNodesBadgeManager.MAX_RANK  - nodePlayer.getCache().getBadge().getRank();
            launchTimes.put(player.getUniqueId(), currentTime + TimeUnit.SECONDS.toMillis(waitTime));
        }
    }

    @EventHandler
    public void onPrimed(ExplosionPrimeEvent event) {
        locations.putIfAbsent(event.getEntity().getEntityId(), event.getEntity().getLocation().toVector().clone());
    }

    @EventHandler
    public void onPrimed(BlockDispenseEvent event) {
        if (event.getItem().getType() != Material.TNT) return;
        fromDispensers.add(event.getVelocity().clone());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPrimed(EntityExplodeEvent event) {
        try {
            if (locations.containsKey(event.getEntity().getEntityId())) {
                Vector current = event.getLocation().clone().toVector();
                Vector old = locations.remove(event.getEntity().getEntityId());

                if (current.clone().setY(old.getY()).distance(old) < 5) {
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
    public void onDeath(GamePlayerDeathEvent event) {
        event.getViewers().clear();
        event.getViewers().addAll(game.getPlayers().collect(Collectors.toList()));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        GamePlayer player = game.getPlayer(event.getEntity());

        game.getPlayingTeams().forEach(team -> {
            if (team != player.getTeam()) {
                addPoint(game, team, 25);

                // Add tokens for other players deaths
                team.getPlayers().forEach(tPlayer -> {
                    if (MapNodesPlugin.getInst().isDebug()) {
                        String tokens = MessageUtil.replaceColors("&7(DEBUG) &b+2 &6tokens ");
                        tPlayer.sendMessage(tokens);
                        MapNodesPlugin.debug(tPlayer.getPlayerColor() + " " + tokens);
                        ((NodePlayer) tPlayer).getCreditsMultiplier().incrementAndGet();
                    }
                    else {
                        tPlayer.sendMessage(MessageUtil.replaceColors("&b+2 &6tokens"));
                        MapNodesPlugin.getInst().getApi().addTokens(tPlayer, 2);
                        ((NodePlayer) tPlayer).getCreditsMultiplier().incrementAndGet();
                    }
                });

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
        if (event.getEntity() != null) {
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
