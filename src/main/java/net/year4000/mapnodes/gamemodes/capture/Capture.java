package net.year4000.mapnodes.gamemodes.capture;

import com.google.common.collect.ImmutableMap;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.events.game.GameLoadEvent;
import net.year4000.mapnodes.api.events.team.GameTeamWinEvent;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.GameRegion;
import net.year4000.mapnodes.api.game.GameTeam;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.api.utils.Spectator;
import net.year4000.mapnodes.game.regions.types.Point;
import net.year4000.mapnodes.gamemodes.GameModeTemplate;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Common;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.utilities.bukkit.BukkitUtil;
import net.year4000.utilities.bukkit.FunEffectsUtil;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@GameModeInfo(
    name = "Capture",
    version = "1.4",
    config = CaptureConfig.class
)
public class Capture extends GameModeTemplate implements GameMode {
    public static final Map<ChatColor, Integer> CHAT_COLOR_DATA_MAP = ImmutableMap.<ChatColor, Integer>builder()
        .put(ChatColor.WHITE, 0)
        .put(ChatColor.GOLD, 1)
        .put(ChatColor.LIGHT_PURPLE, 2)
        .put(ChatColor.BLUE, 11)
        .put(ChatColor.AQUA, 3)
        .put(ChatColor.YELLOW, 4)
        .put(ChatColor.GREEN, 5)
        .put(ChatColor.DARK_GRAY, 7)
        .put(ChatColor.GRAY, 8)
        .put(ChatColor.DARK_AQUA, 9)
        .put(ChatColor.DARK_PURPLE, 10)
        .put(ChatColor.DARK_BLUE, 11)
        .put(ChatColor.DARK_GREEN, 13)
        .put(ChatColor.DARK_RED, 14)
        .put(ChatColor.RED, 14)
        .put(ChatColor.BLACK, 15)
        .build();

    // Game Mode vars
    private CaptureConfig gameModeConfig;
    private Map<String, List<CaptureConfig.BlockCapture>> captures = new HashMap<>();
    private GameManager game;

    @EventHandler
    public void onLoad(GameLoadEvent event) {
        game = event.getGame();
        gameModeConfig = getConfig();
        gameModeConfig.validate(); // This will assign the var maps
        game.addStartTime(60);

        // Set up internal tracking of wools
        gameModeConfig.getBlockCaptures().forEach(capture -> {
            capture.setGrabbed(new ArrayList<>());
            if (captures.containsKey(capture.getChallenger())) {
                captures.get(capture.getChallenger()).add(capture);
            }
            else {
                List<CaptureConfig.BlockCapture> list = new ArrayList<>();
                list.add(capture);
                captures.put(capture.getChallenger(), list);
            }
        });

        boolean firstPadding = false;

        // Display the scoreboard
        for (Map.Entry<String, List<CaptureConfig.BlockCapture>> scores : captures.entrySet()) {
            String team = scores.getKey();
            List<CaptureConfig.BlockCapture> list = scores.getValue();

            if (!firstPadding) {
                firstPadding = true;
            }
            else {
                game.addStaticGoal(String.valueOf(team.hashCode()), "");
            }

            GameTeam nodeTeam = game.getTeams().get(team);
            game.addStaticGoal(team + "-capture", team, nodeTeam.getDisplayName() + " Goals");

            list.forEach(capture -> game.addStaticGoal(getCaptureID(nodeTeam, capture), " " + getCaptureDisplay(capture)));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        GamePlayer player = game.getPlayer(event.getPlayer());
        GameTeam team = player.getTeam();
        Point point = new Point(event.getBlockPlaced().getLocation().toVector().toBlockVector());

        captures.values().forEach(c -> c.forEach(capture -> {
            GameRegion region = game.getRegions().get(capture.getRegion());

            if (capture.getOwner().equals(team.getId()) && region.inZone(point)) {
                event.setCancelled(true);
            }
        }));

        for (CaptureConfig.BlockCapture capture : captures.get(team.getId())) {
            GameRegion region = game.getRegions().get(capture.getRegion());

            if (region.inZone(point)) {
                // MapNodesPlugin.log("%s | %s", capture.getData(), event.getBlockPlaced().getData());

                // Check material
                if (event.getBlockPlaced().getType() != capture.getBlock()) {
                    event.setCancelled(true);
                    return;
                }

                // Check data
                if ((int) event.getBlockPlaced().getData() != capture.getData()) {
                    event.setCancelled(true);
                    return;
                }

                // We capture the goal
                event.setCancelled(false);
                capture.setDone(true);
                game.getSidebarGoals().get(getCaptureID(team, capture)).setDisplay(" " + getCaptureDisplay(capture));

                // Broadcast message
                game.getPlaying().forEach(p -> {
                    Common.sendAnimatedActionBar(p, Msg.locale(p, "capture.placed", player.getPlayerColor(), team.getDisplayName()));
                    FunEffectsUtil.playSound(p.getPlayer(), Sound.NOTE_PLING);
                });

                // Show firework in the block
                Location center = event.getBlockPlaced().getLocation().clone().add(0.5, -0.5, 0.5);
                Firework firework = MapNodes.getCurrentWorld().spawn(center, Firework.class);
                FireworkEffect effect = FireworkEffect.builder()
                    .withColor(BukkitUtil.dyeColorToColor(BukkitUtil.chatColorToDyeColor(capture.getPrefix())))
                    .withFade(BukkitUtil.dyeColorToColor(BukkitUtil.chatColorToDyeColor(capture.getPrefix())))
                    .with(FireworkEffect.Type.BURST)
                    .build();
                FireworkMeta meta = firework.getFireworkMeta();
                meta.clearEffects();
                meta.addEffect(effect);
                meta.setPower(0);
                firework.setFireworkMeta(meta);

                // Check is should win then break loop
                SchedulerUtil.runSync(this::shouldWin);
                break;
            }
        }
    }

    @EventHandler
    public void onPickupWool(PlayerPickupItemEvent event) {
        if (!MapNodes.getCurrentGame().getStage().isPlaying()) {
            return;
        }

        GamePlayer player = game.getPlayer(event.getPlayer());
        GameTeam team = player.getTeam();
        ItemStack item = event.getItem().getItemStack();

        checkPickUpCapture(team, player, item.getType(), item.getData().getData());
    }

    @EventHandler
    public void onPickupWool(InventoryClickEvent event) {
        if (!MapNodes.getCurrentGame().getStage().isPlaying() || event.getCurrentItem() == null) {
            return;
        }

        GamePlayer player = game.getPlayer((Player) event.getWhoClicked());
        GameTeam team = player.getTeam();

        if (team instanceof Spectator) {
            return;
        }

        checkPickUpCapture(team, player, event.getCurrentItem().getType(), event.getCurrentItem().getData().getData());
    }

    /** Check if the wool has been picked up by a player and do things with it */
    private void checkPickUpCapture(GameTeam team, GamePlayer player, Material material, byte data) {
        for (CaptureConfig.BlockCapture capture : captures.get(team.getId())) {
            // Check material
            if (material != capture.getBlock()) {
                continue;
            }

            // Check data
            if ((int) data != capture.getData()) {
                continue;
            }

            if (!capture.getGrabbed().contains(player)) {
                capture.getGrabbed().add(player);
                game.getSidebarGoals().get(getCaptureID(team, capture)).setDisplay(" " + getCaptureDisplay(capture));

                // Show grabbed message
                game.getPlaying().forEach(p -> {
                    Common.sendAnimatedActionBar(p, Msg.locale(p, "capture.grabbed", player.getPlayerColor(), team.getDisplayName()));
                    FunEffectsUtil.playSound(p.getPlayer(), Sound.NOTE_PLING);
                });
                break;
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Point point = new Point(event.getBlock().getLocation().toVector().toBlockVector());

        captures.values().forEach(c -> c.forEach(capture -> {
            GameRegion region = game.getRegions().get(capture.getRegion());

            if (region.inZone(point)) {
                event.setCancelled(true);
            }
        }));
    }

    /** Call the win event if the game should be won */
    private void shouldWin() {
        for (Map.Entry<String, List<CaptureConfig.BlockCapture>> tracker : captures.entrySet()) {
            List<CaptureConfig.BlockCapture> blockCaptures = tracker.getValue();

            if (blockCaptures.stream().filter(CaptureConfig.BlockCapture::isDone).count() == blockCaptures.size()) {
                new GameTeamWinEvent(game, game.getTeams().get(tracker.getKey())).call();
                break;
            }
        }
    }

    /** Get the capture id to use for goal manager */
    private String getCaptureID(GameTeam team, CaptureConfig.BlockCapture capture) {
        return team.getName() + "-" + capture.getName();
    }

    /** Get the capture display based on the stage */
    private String getCaptureDisplay(CaptureConfig.BlockCapture capture) {
        String stage = "";
        String flag = ChatColor.RED + "\u2690";

        if (capture.isDone()) {
            stage = ChatColor.STRIKETHROUGH.toString();
            flag = ChatColor.GREEN + "\u2691";
        }
        else if (capture.getGrabbed().size() > 0) {
            stage = ChatColor.ITALIC.toString();
            flag = ChatColor.YELLOW + "\u2690";
        }

        return flag + " " + capture.getPrefix().toString() + stage + capture.getName();
    }
}
