package net.year4000.mapnodes.gamemodes.spleef;

import com.google.common.collect.ImmutableList;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.events.game.GameLoadEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerDeathEvent;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.clocks.Clocker;
import net.year4000.mapnodes.gamemodes.elimination.Elimination;
import net.year4000.mapnodes.utils.MathUtil;
import net.year4000.mapnodes.utils.SchedulerUtil;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@GameModeInfo(
    name = "SpleefRunner",
    version = "1.0",
    config = SpleefRunnerConfig.class
)
public class SpleefRunner extends Elimination {
    private static final int END_STAGE = 0; // todo increase when we can get block cracks
    private static final Random rand = new Random();
    public static final ImmutableList DATA_BLOCKS = ImmutableList.of(Material.STAINED_CLAY, Material.WOOL, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE);
    private Map<BlockVector, AtomicInteger> blockStages = new HashMap<>();
    private SpleefRunnerConfig spleefConfig;
    private List<Integer> ids = new ArrayList<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void onLoadSpleefRunner(GameLoadEvent event) {
        spleefConfig = getConfig();
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
        if (game.getStage().isPlaying() && event.getPlayer().getItemInHand().getType() == Material.IRON_HOE) {
            Snowball entity = event.getPlayer().launchProjectile(Snowball.class);
            ids.add(entity.getEntityId());
        }
    }

    @EventHandler
    public void onHit(ProjectileHitEvent e) {
        if (ids.contains(e.getEntity().getEntityId())) {
            Location loc = e.getEntity().getLocation();
            List<BlockFace> faces = ImmutableList.of(BlockFace.UP, BlockFace.SELF, BlockFace.DOWN);

            for (BlockFace face : faces) {
                Block bellow = loc.getBlock().getRelative(face);
                BlockVector bellowVector = bellow.getLocation().toVector().toBlockVector();

                if (spleefConfig.getBlocks().contains(bellow.getType())) {
                    FallingBlock fallingBlock = MapNodes.getCurrentWorld().spawnFallingBlock(bellow.getLocation(), bellow.getType(), bellow.getData());
                    bellow.setType(Material.AIR);
                    fallingBlock.setDropItem(false);
                }

                blockStages.remove(bellowVector);
            }

            MapNodes.getCurrentGame().getPlaying()
                .filter(player -> player.getPlayer().getLocation().distance(loc) < 100)
                .map(GamePlayer::getPlayer)
                .forEach(player -> player.playSound(loc, Sound.CHICKEN_EGG_POP, 1F, 1F));

            ids.remove((Integer) e.getEntity().getEntityId());
        }
    }

    @EventHandler
    public void onRunner(PlayerMoveEvent event) {
        BlockVector vector = event.getFrom().toVector().toBlockVector();

        if (event.getTo().toVector().toBlockVector().equals(vector)) return;

        GamePlayer player = game.getPlayer(event.getPlayer());

        if (!player.isPlaying()) return;

        Block bellow = event.getTo().getBlock().getRelative(BlockFace.DOWN);
        BlockVector bellowVector = bellow.getLocation().toVector().toBlockVector();

        blockStages.putIfAbsent(bellowVector, new AtomicInteger(0));
        int stage = blockStages.get(bellowVector).getAndIncrement();

        if (spleefConfig.getBlocks().contains(bellow.getType()) && stage == END_STAGE) {
            new Clocker(MathUtil.ticks(6)) {
                private byte[] stage = new byte[] {0, 14, 1, 4, 5, 13};

                @Override
                public void runTock(int position) {
                    if (position % 20 != 0) return;

                    if (!DATA_BLOCKS.contains(bellow.getType())) {
                        bellow.setType(Material.STAINED_CLAY);
                    }

                    bellow.setData(stage[MathUtil.sec(position)]);
                    MapNodes.getCurrentWorld().playEffect(bellow.getLocation(), Effect.SNOW_SHOVEL);
                }

                @Override
                public void runLast(int position) {
                    FallingBlock fallingBlock = MapNodes.getCurrentWorld().spawnFallingBlock(bellow.getLocation(), bellow.getType(), bellow.getData());
                    bellow.setType(Material.AIR);
                    fallingBlock.setDropItem(false);
                    blockStages.remove(bellowVector);

                    MapNodes.getCurrentGame().getPlaying()
                        .filter(player -> player.getPlayer().getLocation().distance(bellow.getLocation()) < 100)
                        .map(GamePlayer::getPlayer)
                        .forEach(player -> player.playSound(bellow.getLocation(), Sound.LAVA_POP, 1F, 1F));
                }
            }.run();
        }
    }
}
