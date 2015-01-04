package net.year4000.mapnodes.gamemodes.tag;

import com.google.common.collect.ImmutableSet;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.events.game.GameClockEvent;
import net.year4000.mapnodes.api.events.game.GameLoadEvent;
import net.year4000.mapnodes.api.events.game.GameStartEvent;
import net.year4000.mapnodes.api.events.game.GameWinEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerJoinEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerJoinTeamEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerStartEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerWinEvent;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.GameTeam;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.api.utils.Spectator;
import net.year4000.mapnodes.clocks.Clocker;
import net.year4000.mapnodes.game.NodeTeam;
import net.year4000.mapnodes.gamemodes.GameModeTemplate;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.MathUtil;
import net.year4000.mapnodes.utils.PacketHacks;
import net.year4000.utilities.bukkit.MessageUtil;
import net.year4000.utilities.bukkit.bossbar.BossBar;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@GameModeInfo(
    name = "Arrow Tag",
    version = "1.0",
    config = ArrowTagConfig.class
)
public class ArrowTag extends GameModeTemplate implements GameMode {
    private static final int MAX = 20;
    private GameManager game;
    private GameTeam team;
    private ConcurrentMap<String, AtomicInteger> scores = new ConcurrentHashMap<>();
    private static final ImmutableSet<EntityDamageEvent.DamageCause> DAMAGE = ImmutableSet.of(EntityDamageEvent.DamageCause.PROJECTILE, EntityDamageEvent.DamageCause.LAVA, EntityDamageEvent.DamageCause.VOID, EntityDamageEvent.DamageCause.ENTITY_ATTACK);

    @EventHandler(priority = EventPriority.HIGH)
    public void onLoad(GameLoadEvent event) {
        game = event.getGame();

        game.loadKit(ArrowTagKit.NAME, new ArrowTagKit());

        team = game.getPlayingTeams().collect(Collectors.toList()).iterator().next();
        team.setAllowFriendlyFire(true); // Force enable so players can kill each other
        team.setSize(14); // Max of 14 players all ways
        ((NodeTeam) team).setKit(ArrowTagKit.NAME);

        // Add requirements | Their must be at least 2 players to start
        game.getStartControls().add(() -> team.getPlayers().size() >= 2);

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onStart(GameStartEvent event) {
        new ArrayList<>(team.getQueue()).forEach(GamePlayer::joinSpectatorTeam);

        game.addDynamicGoal("tag", MessageUtil.replaceColors("&7-- &6WIN &7--"), MAX);
        team.getPlayers().stream()
            .forEach(player -> {
                String id = getPlayerId(player.getPlayer());
                scores.putIfAbsent(id, new AtomicInteger(0));
                game.addDynamicGoal(id, MessageUtil.replaceColors("&e" + player.getPlayer().getName()), scores.get(id).get());
            });
    }

    @EventHandler()
    public void onStartPlayer(GamePlayerStartEvent event) {
        event.setImmortal(false);
        event.getMessage().add(Msg.locale(event.getPlayer(), "arrow_tag.start.win"));
        event.getMessage().add(Msg.locale(event.getPlayer(), "arrow_tag.start.kill"));
    }

    @EventHandler()
    public void onStart(GameWinEvent event) {
        List<String> winners = new ArrayList<>(scores.keySet());
        winners.sort((l, r) -> scores.get(l).get() < scores.get(r).get() ? 1 : -1);
        List<String> message = event.getMessage();

        if (winners.size() < 3) {
            winners.forEach(name -> message.add(MessageUtil.replaceColors("&a" + name.split("-")[1] + " &7(&e" + scores.get(name).get() + "&7)")));
        }
        else {
            winners.subList(0, 3).forEach(name -> message.add(MessageUtil.replaceColors("&a" + name.split("-")[1] + " &7(&e" + scores.get(name).get() + "&7)")));
        }
    }

    @EventHandler
    public void onBowUse(EntityShootBowEvent event) {
        if (event.getEntity().getLocation().getY() < 0) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity().getLocation().getY() < 0) {
            event.setCancelled(true);
            return;
        }

        if (!DAMAGE.contains(event.getCause())) {
            event.setCancelled(true);
        }

        if (event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            ((Player) event.getEntity()).setHealth(0);
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

    /** Don't allow players to select teams when the game started */
    @EventHandler
    public void onTeamSelect(GamePlayerJoinTeamEvent event) {
        if (event.getTo() instanceof Spectator) return;

        if (game.getStage().isPlaying()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Msg.NOTICE + Msg.locale(event.getPlayer(), "elimination.join.playing"));
        }
    }

    /** Disable menu when game started */
    @EventHandler
    public void onPlayerJoin(GamePlayerJoinEvent event) {
        if (game.getStage().isPlaying()) {
            event.setMenu(false);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getPlayer().getLocation().getY() < 0) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onToggleFlight(PlayerToggleFlightEvent event) {
        if (event.getPlayer().getLocation().getY() < 0) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLeave(GameClockEvent event) {
        if (event.getGame().getPlaying().count() <= 1L && !MapNodes.getLogUtil().isDebug()) {
            game.stop();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        Location hide = game.getConfig().getSpawn().get(0).clone();
        hide.setYaw(90);
        hide.setY(-20);
        event.setRespawnLocation(hide);
        final Player player = event.getPlayer();

        game.getPlayer(player).getPlayerTasks().add(new Clocker(MathUtil.ticks(15)) {
            @Override
            public void runFirst(int position) {
                player.setAllowFlight(true);
                player.setFlying(true);
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, MathUtil.ticks(1), 3, true), true);
                player.setFireTicks(0);
            }

            @Override
            public void runTock(int position) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, MathUtil.ticks(1), 3, true), true);
                player.setExp(1);

                if (MathUtil.sec(position) == 0) return;

                String counter = MessageUtil.replaceColors("&a" + MathUtil.sec(position));

                if (PacketHacks.isTitleAble(player)) {
                    PacketHacks.setTitle(player, counter, "");
                }
                else {
                    BossBar.setMessage(player, counter, 0.001F);
                }
            }

            @Override
            public void runLast(int position) {
                player.setFlying(false);
                player.setAllowFlight(false);
                player.teleport(team.getSafeRandomSpawn());
            }
        }.run());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();
        PacketHacks.respawnPlayer(player);

        if (killer == null) return;

        int score = addScore(killer);

        if (score >= MAX) {
            new GamePlayerWinEvent(game, game.getPlayer(killer)).call();
        }
    }

    private int addScore(Player player) {
        int score = scores.get(getPlayerId(player)).incrementAndGet();
        game.getSidebarGoals().get(getPlayerId(player)).setScore(score);

        ItemStack item = player.getInventory().getItem(ArrowTagKit.ARROW_SLOT);

        if (item == null) {
            item = new ItemStack(Material.ARROW);
            ItemMeta metaArrow = item.getItemMeta();
            metaArrow.setDisplayName(ArrowTagKit.ITEM_NAME_ALT);
            item.setItemMeta(metaArrow);
        }
        else {
            item.setAmount(item.getAmount() + 1);
        }

        player.getInventory().setItem(ArrowTagKit.ARROW_SLOT, item);

        return score;
    }

    private String getPlayerId(Player player) {
        return "tag-" + player.getName();
    }
}
