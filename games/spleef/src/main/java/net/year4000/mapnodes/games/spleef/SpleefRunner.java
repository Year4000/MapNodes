/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.games.spleef;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.events.game.GameLoadEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerDeathEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerStartEvent;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.clocks.Clocker;
import net.year4000.mapnodes.games.elimination.Elimination;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.MathUtil;
import net.year4000.mapnodes.utils.PacketHacks;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@GameModeInfo(
    name = "Spleef Runner",
    version = "2.2",
    config = SpleefRunnerConfig.class,
    listeners = {SpleefPowerUp.class}
)
public class SpleefRunner extends Elimination {
    private static final int END_STAGE = 3;
    private static final Random rand = new Random();
    public static final ImmutableList<BlockFace> FACES = ImmutableList.of(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH_EAST, BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST);
    public static final ImmutableList DATA_BLOCKS = ImmutableList.of(Material.STAINED_CLAY, Material.WOOL, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE);
    private Map<BlockVector, AtomicInteger> blockStages = new HashMap<>();
    private SpleefRunnerConfig spleefConfig;

    @EventHandler(priority = EventPriority.HIGH)
    public void onLoadSpleefRunner(GameLoadEvent event) {
        spleefConfig = getConfig();
    }

    @EventHandler
    public void onJoin(GamePlayerStartEvent event) {
        GamePlayer player = event.getPlayer();
        ItemStack item = event.getKit().getItems().get(0).clone();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageUtil.replaceColors(meta.getDisplayName() + " &7(" + Msg.locale(event.getPlayer(), "action.right") + ")"));
        item.setItemMeta(meta);

        player.addPlayerData(UserData.class, new UserData(player));
        event.getPlayer().getPlayerTasks().add(SchedulerUtil.runSync(() -> {
            event.getPlayer().getPlayer().getInventory().setItem(0, item);
            event.getPlayer().getPlayer().updateInventory();
        }, 10L));
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.VOID) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrop(EntitySpawnEvent event) {
        if (event.getEntityType() == EntityType.ARMOR_STAND) {
            event.setCancelled(false);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onGameDeath(GamePlayerDeathEvent event) {
        event.getViewers().clear();
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockLand(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof org.bukkit.entity.FallingBlock) {
            if (event.getBlock().getType() == Material.AIR) {
                SchedulerUtil.runSync(() -> {
                    event.getBlock().setType(Material.AIR);
                    Location loc = event.getBlock().getLocation();
                    MapNodes.getCurrentGame().getPlaying()
                        .filter(player -> player.getPlayer().getLocation().distance(loc) < 100)
                        .map(GamePlayer::getPlayer)
                        .forEach(player -> {
                            for (int i = 0; i < 10; i++) {
                                player.playEffect(splater(loc), Effect.VOID_FOG, 1);
                            }
                        });
                });
            }
        }
    }

    private Location splater(Location loc) {
        return loc.clone().add(new Vector(rand.nextDouble(), rand.nextDouble() + 0.5, rand.nextDouble()));
    }

    @EventHandler
    public void use(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR)  {
            event.setCancelled(true);
            return;
        }

        if (game.getStage().isPlaying() && event.getPlayer().getItemInHand().getType() == Material.IRON_HOE) {
            GamePlayer gamePlayer = MapNodes.getCurrentGame().getPlayer(event.getPlayer());
            UserData data = gamePlayer.getPlayerData(UserData.class);
            Location loc = event.getPlayer().getEyeLocation().clone().add(event.getPlayer().getLocation().getDirection().normalize());
            List<Map.Entry<Vector, Location>> snowballs = Lists.newArrayList();

            if (data.getAmount() == 2) {
                Location right = loc.clone();
                right.setYaw(loc.getYaw() + 5);
                snowballs.add(new AbstractMap.SimpleImmutableEntry<>(right.getDirection(), loc));

                Location left = loc.clone();
                left.setYaw(loc.getYaw() - 5);
                snowballs.add(new AbstractMap.SimpleImmutableEntry<>(left.getDirection(), loc));
            }
            else if (data.getAmount() == 3) {
                Location right = loc.clone();
                right.setYaw(loc.getYaw() + 10);
                snowballs.add(new AbstractMap.SimpleImmutableEntry<>(right.getDirection(), loc));

                snowballs.add(new AbstractMap.SimpleImmutableEntry<>(loc.getDirection(), loc));

                Location left = loc.clone();
                left.setYaw(loc.getYaw() - 10);
                snowballs.add(new AbstractMap.SimpleImmutableEntry<>(left.getDirection(), loc));
            }
            else {
                snowballs.add(new AbstractMap.SimpleImmutableEntry<>(loc.getDirection(), loc));
            }

            snowballs.stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                .forEach((velocity, location) -> {
                    Entity snowball = loc.getWorld().spawnEntity(location, EntityType.SNOWBALL);
                    snowball.setMetadata("SpleefRunner", new FixedMetadataValue(MapNodesPlugin.getInst(), true));
                    snowball.setVelocity(velocity.multiply(data.getPower()));
                });
        }
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        Entity entity = event.getEntity();
        if (!entity.hasMetadata("SpleefRunner")) return;

        Vector projectile = entity.getVelocity().normalize();
        Location loc = entity.getLocation().add(projectile);

        // Convert block to falling block
        Block bellow = entity.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        if (spleefConfig.getBlocks().contains(bellow.getType())) {
            FallingBlock fallingBlock = MapNodes.getCurrentWorld().spawnFallingBlock(loc, bellow.getType(), bellow.getData());
            bellow.setType(Material.AIR);
            fallingBlock.setDropItem(false);
        }

        // Play sound to players
        MapNodes.getCurrentGame().getPlaying()
            .filter(player -> player.getPlayer().getLocation().distance(loc) < 100)
            .map(GamePlayer::getPlayer)
            .forEach(player -> player.playSound(loc, Sound.CHICKEN_EGG_POP, 1F, 1F));
    }

    @EventHandler
    public void onRunner(PlayerMoveEvent event) {
        BlockVector vector = event.getFrom().toVector().toBlockVector();

        if (event.getTo().toVector().toBlockVector().equals(vector)) return;

        GamePlayer player = game.getPlayer(event.getPlayer());

        if (!player.isPlaying()) return;

        // Track the current block id and give crack value
        Block bellow = event.getTo().getBlock().getRelative(BlockFace.DOWN);
        Map.Entry<BlockVector, Integer> tracking = trackBlock(bellow);
        BlockVector bellowVector = tracking.getKey();
        int stage = tracking.getValue();
        PacketHacks.crackBlock(bellow, stage);

        if (spleefConfig.getBlocks().contains(bellow.getType()) && stage >= END_STAGE) {
            runClock(bellow, bellowVector);
        }
    }

    private void runClock(Block bellow, BlockVector bellowVector) {
        new Clocker(MathUtil.ticks(6)) {
            private byte[] stage = new byte[] {0, 14, 1, 4, 5, 13};

            @Override
            public void runTock(int position) {
                if (position % 20 != 0 || !bellow.getType().isSolid()) return;

                if (!DATA_BLOCKS.contains(bellow.getType())) {
                    bellow.setType(Material.STAINED_CLAY);
                }

                bellow.setData(stage[MathUtil.sec(position)]);
                if (rand.nextBoolean()) {
                    MapNodes.getCurrentWorld().spigot().playEffect(bellow.getLocation(), Effect.SNOW_SHOVEL);
                }
            }

            @Override
            public void runLast(int position) {
                if (!bellow.getType().isSolid()) return;

                FallingBlock fallingBlock = MapNodes.getCurrentWorld().spawnFallingBlock(bellow.getLocation(), bellow.getType(), bellow.getData());
                bellow.setType(Material.AIR);
                fallingBlock.setDropItem(false);
                blockStages.remove(bellowVector);

                MapNodes.getCurrentGame().getPlaying()
                    .filter(player -> player.getPlayer().getLocation().distance(bellow.getLocation()) < 100)
                    .map(GamePlayer::getPlayer)
                    .forEach(player -> player.playSound(bellow.getLocation(), Sound.LAVA_POP, 1F, 1F));

                MapNodes.getCurrentGame().getPlaying()
                    .filter(player -> player.getPlayer().getLocation().distance(bellow.getLocation()) <= 2)
                    .map(GamePlayer::getPlayer)
                    .forEach(player -> {
                        for (BlockFace face : FACES) {
                            Block bellowRelative = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getRelative(face);
                            Map.Entry<BlockVector, Integer> trackingRelative = trackBlock(bellowRelative);
                            BlockVector bellowRelativeVector = trackingRelative.getKey();
                            int stageRelative = trackingRelative.getValue();

                            if (spleefConfig.getBlocks().contains(bellowRelative.getType()) && stageRelative == END_STAGE) {
                                runClock(bellowRelative, bellowRelativeVector);
                            }
                        }
                    });
            }
        }.run();
    }

    private Map.Entry<BlockVector, Integer> trackBlock(Block block) {
        BlockVector bellowVector = block.getLocation().toVector().toBlockVector();

        blockStages.putIfAbsent(bellowVector, new AtomicInteger(0));
        int stage = blockStages.get(bellowVector).getAndAdd(3);

        return new AbstractMap.SimpleImmutableEntry<>(bellowVector, stage);
    }
}
