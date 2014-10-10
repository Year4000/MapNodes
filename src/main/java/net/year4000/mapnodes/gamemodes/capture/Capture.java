package net.year4000.mapnodes.gamemodes.capture;

import com.google.common.collect.ImmutableMap;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.events.game.GameLoadEvent;
import net.year4000.mapnodes.api.events.team.GameTeamWinEvent;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.game.NodeGame;
import net.year4000.mapnodes.game.NodePlayer;
import net.year4000.mapnodes.game.NodeRegion;
import net.year4000.mapnodes.game.NodeTeam;
import net.year4000.mapnodes.game.regions.types.Point;
import net.year4000.mapnodes.game.system.Spectator;
import net.year4000.mapnodes.gamemodes.GameModeTemplate;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.utilities.bukkit.FunEffectsUtil;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryClickedEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@GameModeInfo(
    name = "Capture",
    version = "1.0",
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
    private NodeGame game;

    @EventHandler
    public void onLoad(GameLoadEvent event) {
        game = (NodeGame) event.getGame();
        gameModeConfig = (CaptureConfig) getConfig();
        gameModeConfig.validate(); // This will assign the var maps

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

            NodeTeam nodeTeam = game.getTeams().get(team);
            game.addStaticGoal(team, team, nodeTeam.getDisplayName() + " Goals");

            list.forEach(capture -> game.addStaticGoal(getCaptureID(nodeTeam, capture), " " + getCaptureDisplay(capture)));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        GamePlayer player = game.getPlayer(event.getPlayer());
        NodeTeam team = ((NodeTeam) player.getTeam());
        Point point = new Point(event.getBlockPlaced().getLocation().toVector().toBlockVector());

        captures.values().forEach(c -> c.forEach(capture -> {
            NodeRegion region = game.getRegions().get(capture.getRegion());

            if (capture.getOwner().equals(team.getId()) && region.inZone(point)) {
                event.setCancelled(true);
            }
        }));

        for (CaptureConfig.BlockCapture capture : captures.get(team.getId())) {
            NodeRegion region = game.getRegions().get(capture.getRegion());

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

                event.setCancelled(false);
                capture.setDone(true);
                game.getSidebarGoals().get(getCaptureID(team, capture)).setDisplay(" " + getCaptureDisplay(capture));
                game.getPlayers().forEach(p -> {
                    p.sendMessage(Msg.locale(p, "capture.placed", player.getPlayerColor(), team.getDisplayName()));
                    game.getScoreboardFactory().setGameSidebar((NodePlayer) p);
                    FunEffectsUtil.playSound(p.getPlayer(), Sound.NOTE_PLING);
                });
                SchedulerUtil.runSync(this::shouldWin);
                break;
            }
        }
    }

    @EventHandler
    public void onPickupWool(PlayerPickupItemEvent event) {
        if (!MapNodes.getCurrentGame().getStage().isPlaying()) return;

        NodePlayer player = (NodePlayer) game.getPlayer(event.getPlayer());
        NodeTeam team = player.getTeam();

        for (CaptureConfig.BlockCapture capture : captures.get(team.getId())) {
            // Check material
            if (event.getItem().getItemStack().getType() != capture.getBlock()) {
                continue;
            }

            // Check data
            if ((int) event.getItem().getItemStack().getData().getData() != capture.getData()) {
                continue;
            }

            if (!capture.getGrabbed().contains(player)) {
                capture.getGrabbed().add(player);
                game.getSidebarGoals().get(getCaptureID(team, capture)).setDisplay(" " + getCaptureDisplay(capture));

                game.getPlayers().forEach(p -> {
                    p.sendMessage(Msg.locale(p, "capture.grabed", player.getPlayerColor(), team.getDisplayName()));
                    game.getScoreboardFactory().setGameSidebar((NodePlayer) p);
                    FunEffectsUtil.playSound(p.getPlayer(), Sound.NOTE_PLING);
                });
            }
        }
    }

    @EventHandler
    public void onPickupWool(InventoryClickEvent event) {
        if (!MapNodes.getCurrentGame().getStage().isPlaying() || event.getCurrentItem() == null) return;

        NodePlayer player = (NodePlayer) game.getPlayer((Player) event.getWhoClicked());
        NodeTeam team = player.getTeam();

        if (team instanceof Spectator) return;

        for (CaptureConfig.BlockCapture capture : captures.get(team.getId())) {
            // Check material
            if (event.getCurrentItem().getType() != capture.getBlock()) {
                continue;
            }

            // Check data
            if ((int) event.getCurrentItem().getData().getData() != capture.getData()) {
                continue;
            }

            if (!capture.getGrabbed().contains(player)) {
                capture.getGrabbed().add(player);
                game.getSidebarGoals().get(getCaptureID(team, capture)).setDisplay(" " + getCaptureDisplay(capture));

                game.getPlayers().forEach(p -> {
                    p.sendMessage(Msg.locale(p, "capture.grabbed", player.getPlayerColor(), team.getDisplayName()));
                    game.getScoreboardFactory().setGameSidebar((NodePlayer) p);
                    FunEffectsUtil.playSound(p.getPlayer(), Sound.NOTE_PLING);
                });
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Point point = new Point(event.getBlock().getLocation().toVector().toBlockVector());

        captures.values().forEach(c -> c.forEach(capture -> {
            NodeRegion region = game.getRegions().get(capture.getRegion());

            if (region.inZone(point)) {
                event.setCancelled(true);
            }
        }));
    }

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
    private String getCaptureID(NodeTeam team, CaptureConfig.BlockCapture capture) {
        return team.getName() + "-" + capture.getName();
    }

    /** Get the capture display based on the stage */
    private String getCaptureDisplay(CaptureConfig.BlockCapture capture) {
        String stage = "";
        if (capture.isDone()) {
            stage = ChatColor.STRIKETHROUGH.toString();
        }
        else if (capture.getGrabbed().size() > 0) {
            stage = ChatColor.ITALIC.toString();
        }

        return capture.getPrefix().toString() + stage + capture.getName();
    }
}
