package net.year4000.mapnodes.listeners;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterators;
import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.events.game.GameClockEvent;
import net.year4000.mapnodes.api.events.game.GameStartEvent;
import net.year4000.mapnodes.api.events.game.GameWinEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerWinEvent;
import net.year4000.mapnodes.api.events.team.GameTeamWinEvent;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.GameTeam;
import net.year4000.mapnodes.api.utils.Spectator;
import net.year4000.mapnodes.game.NodeGame;
import net.year4000.mapnodes.game.NodeKit;
import net.year4000.mapnodes.game.NodePlayer;
import net.year4000.mapnodes.game.NodeTeam;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Common;
import net.year4000.mapnodes.utils.PacketHacks;
import net.year4000.mapnodes.utils.typewrappers.LocationList;
import net.year4000.utilities.ChatColor;
import net.year4000.utilities.bukkit.FunEffectsUtil;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EqualsAndHashCode
public final class GameListener implements Listener {
    @EventHandler
    public void respawn(PlayerRespawnEvent event) {
        GamePlayer player = MapNodes.getCurrentGame().getPlayer(event.getPlayer());

        if (player.isPlaying()) {
            if (((NodePlayer) player).hasClassKit()) {
                ((NodePlayer) player).getClassKit().getKit().giveKit(player);
            }
            else {
                player.getTeam().getKit().giveKit(player);
            }

            ((NodePlayer) player).updateInventories();

            // God buffer mode
            if (((NodePlayer) player).isImmortal()) {
                player.getPlayerTasks().add(NodeKit.immortal(event.getPlayer()));
            }
        }

        if (player.getTeam() == null) {
            event.setRespawnLocation(((LocationList) MapNodes.getCurrentGame().getTeams().get(NodeTeam.SPECTATOR).getSpawns()).getSafeRandomSpawn());
        }
        else {
            event.setRespawnLocation(((NodeTeam) player.getTeam()).getSpawns().getSafeRandomSpawn());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWin(GameWinEvent event) {
        if (event.getGame().getStage().isEndGame()) return;

        final int size = 45;
        // Send messages to players before game end
        // Spectator Messages
        Stream.concat(event.getGame().getSpectating(), event.getGame().getEntering()).forEach(player -> {
            player.sendMessage("");
            player.sendMessage(Common.textLine(Msg.locale(player, "game.end"), 40, '*'));
            player.sendMessage(Common.textLine(Msg.locale(player, "game.end.winner", event.getWinnerText()), size, ' ', "", "&a"));
            player.sendMessage("&7&m******************************************");
            player.sendMessage("");
        });

        // Game Player Messages
        event.getGame().getPlaying().forEach(player -> {
            FunEffectsUtil.playSound(player.getPlayer(), Sound.FIREWORK_BLAST2);
            FunEffectsUtil.playSound(player.getPlayer(), Sound.FIREWORK_TWINKLE2);

            player.sendMessage("");
            player.sendMessage(Common.textLine(Msg.locale(player, "game.end"), 40, '*'));
            String winnerText = event.getWinnerText(), endComment = null;
            int xp = 250;
            int tokens = 10;

            // Color winnerText and add endComment
            if (event instanceof GameTeamWinEvent) {
                if (player.getTeam().equals(((GameTeamWinEvent) event).getWinner())) {
                    winnerText = Common.fcolor(ChatColor.ITALIC, winnerText);
                    endComment = Msg.locale(player, "game.end.team_winner");
                    xp += 300;
                    tokens += 25;
                }
                else {
                    endComment = Msg.locale(player, "game.end.team_loser");
                    xp += 100;
                    tokens += 5;
                }
            }
            else if (event instanceof GamePlayerWinEvent) {
                if (player.equals(((GamePlayerWinEvent) event).getWinner())) {
                    xp += 300;
                    tokens += 25;
                }
            }

            player.sendMessage(Common.textLine(Msg.locale(player, "game.end.winner", winnerText), size, ' ', "", "&a"));

            // Show comment to see if you won or lost
            if (endComment != null) {
                player.sendMessage(Common.textLine(endComment, size, ' ', "", ""));
            }

            if (event.getMessage().size() > 0) {
                player.sendMessage("");
                event.getMessage().forEach(string -> player.sendMessage(Common.textLine(string, size, ' ', "", "&a&o")));
            }

            player.sendMessage("&7&m******************************************");
            if (MapNodesPlugin.getInst().isDebug()) {
                player.sendMessage(Common.textLine("&7(DEBUG) &a+" + xp + " &6xp", size, ' ', "", ""));
                player.sendMessage(Common.textLine("&7(DEBUG) &a+" + tokens + " &6tokens", size, ' ', "", ""));
                MapNodesPlugin.debug("Would have added " + xp + " xp to " + player.getPlayerColor());
                MapNodesPlugin.debug("Would have added " + tokens + " tokens to " + player.getPlayerColor());
            }
            else {
                player.sendMessage(Common.textLine("&a+" + xp + " &6xp", size, ' ', "", ""));
                MapNodesPlugin.getInst().getApi().addExperience(player, xp);
                player.sendMessage(Common.textLine("&b+" + tokens + " &6tokens", size, ' ', "", ""));
                MapNodesPlugin.getInst().getApi().addTokens(player, tokens);
            }
            player.sendMessage("&7&m******************************************");
            player.sendMessage("");
        });

        // Stop the game
        MapNodes.getCurrentGame().stop();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onClock(GameClockEvent event) {
        if (event.getGame().getStage().isEndGame()) return;

        event.getGame().getPlayers().parallel().forEach(player -> {
            if (!PacketHacks.isTitleAble(player.getPlayer()) && !player.isPlaying()) {
                ((NodeGame) event.getGame()).getScoreboardFactory().setPersonalSidebar((NodePlayer) player);
            }
        });

        // If not in debug mode check if their are still players.
        if (!MapNodesPlugin.getInst().getLog().isDebug()) {
            // Ensure their is at least one player on each team else just end the game
            int teamSize = (int) (event.getGame()).getPlayingTeams().count();

            // Its a custom game mode let the game mode handle early ends
            if (teamSize == 1) return;

            List<String> left = (event.getGame()).getPlayingTeams()
                .filter(team -> team.getPlaying() > 0)
                .map(GameTeam::getDisplayName)
                .collect(Collectors.toList());

            if (left.size() != teamSize) {
                if (left.size() == 0) {
                    left.addAll(event.getGame().getTeams().values().stream().filter(t -> t instanceof Spectator).map(GameTeam::getDisplayName).collect(Collectors.toList()));
                }

                new GameWinEvent() {{
                    game = event.getGame();
                    winnerText = MessageUtil.replaceColors(Joiner.on("&7, ").join(left));
                }}.call();
            }
        }
    }

    /** The world height cap. */
    @EventHandler(ignoreCancelled = true)
    public void onHeight(BlockPlaceEvent event) {
        int height = MapNodes.getCurrentGame().getConfig().getWorldHeight();

        if (height > 0) {
            int y = event.getBlockPlaced().getY();

            if (y >= height) {
                event.getPlayer().sendMessage(Msg.NOTICE + Msg.locale(event.getPlayer(), "region.deny.height", String.valueOf(y)));
                event.setCancelled(true);
            }
        }
    }

    // Update Player's Inventory

    @EventHandler(priority = EventPriority.MONITOR)
    public void updateInventory(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            GamePlayer player = MapNodes.getCurrentGame().getPlayer((Player) event.getEntity());
            ((NodePlayer) player).updateInventories();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void updateInventory(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            GamePlayer player = MapNodes.getCurrentGame().getPlayer((Player) event.getWhoClicked());
            ((NodePlayer) player).updateInventories();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void updateInventory(PlayerPickupItemEvent event) {
        GamePlayer player = MapNodes.getCurrentGame().getPlayer(event.getPlayer());
        ((NodePlayer) player).updateInventories();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void updateInventory(PlayerDropItemEvent event) {
        GamePlayer player = MapNodes.getCurrentGame().getPlayer(event.getPlayer());
        ((NodePlayer) player).updateInventories();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void updateInventory(PlayerItemConsumeEvent event) {
        GamePlayer player = MapNodes.getCurrentGame().getPlayer(event.getPlayer());
        ((NodePlayer) player).updateInventories();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void updateInventory(PlayerItemBreakEvent event) {
        GamePlayer player = MapNodes.getCurrentGame().getPlayer(event.getPlayer());
        ((NodePlayer) player).updateInventories();
    }
}
