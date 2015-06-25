/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.gamemodes.destory;

import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.api.events.game.GameLoadEvent;
import net.year4000.mapnodes.api.events.team.GameTeamWinEvent;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.GameRegion;
import net.year4000.mapnodes.api.game.GameTeam;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.game.NodePlayer;
import net.year4000.mapnodes.game.regions.types.Point;
import net.year4000.mapnodes.gamemodes.GameModeTemplate;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Common;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.utilities.bukkit.FunEffectsUtil;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.List;
import java.util.stream.Collectors;

@GameModeInfo(
    name = "Destroy",
    version = "1.0",
    config = DestroyConfig.class
)
public class Destroy extends GameModeTemplate implements GameMode {
    private DestroyConfig gameModeConfig;
    private GameManager game;

    @EventHandler
    public void onLoad(GameLoadEvent event) {
        gameModeConfig = getConfig();
        game = event.getGame();
        game.addStartTime(60);

        game.getPlayingTeams().forEach(team -> {
            game.addStaticGoal(team.getId() + "-destroy", team.getId(), team.getDisplayName() + "'s Targets");
            gameModeConfig.getChallengerTargets(team.getId()).forEach(target -> {
                target.init(game);
                game.addStaticGoal(target.getId(), target.getDisplay());
            });
        });
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent event) {
        GamePlayer player = game.getPlayer(event.getPlayer());
        GameTeam team = player.getTeam();

        event.setCancelled(destroyTarget(player, team, event.isCancelled(), event.getBlock()));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(EntityExplodeEvent event) {
        GamePlayer player = game.getPlayer(Bukkit.getOnlinePlayers().iterator().next());
        GameTeam team = player.getTeam();

        event.setCancelled(destroyTarget(player, team, event.isCancelled(), event.blockList().toArray(new Block[event.blockList().size()])));
    }

    public boolean destroyTarget(GamePlayer player, GameTeam team, Block... blocks) {
        return destroyTarget(player, team, false, blocks);
    }

    public boolean destroyTarget(GamePlayer player, GameTeam team, boolean cancel, Block... blocks) {
        DestroyTarget goal = null;

        for (Block block : blocks) {
            Point point = new Point(block.getLocation().toVector().toBlockVector());

            // The owner stop the event
            for (DestroyTarget target : gameModeConfig.getTargets()) {
                GameRegion region = game.getRegions().get(target.getRegion());

                if (target.getOwner().equals(team.getId()) && region.inZone(point)) {
                    cancel = true;
                    break;
                }
            }

            // The Challenger check if broken
            for (DestroyTarget target : gameModeConfig.getChallengerTargets((player.getTeam()).getId())) {
                if (target.getNodeRegion().inZone(point) && target.getStage() != DestroyTarget.Stage.END) {
                    cancel = false;
                    target.updateProgress(blocks);
                    goal = target;
                    block.getDrops().clear();
                    break;
                }
            }
        }

        if (goal != null) {
            updateDisplays(goal, player);
        }

        return cancel;
    }

    /** Update the target and send the display with sound to the playing players */
    private void updateDisplays(DestroyTarget target, GamePlayer gamePlayer) {
        game.getPlayingTeams().forEach(team -> {
            game.addStaticGoal(team.getId() + "-destroy", team.getId(), team.getDisplayName() + "'s Targets");
            gameModeConfig.getChallengerTargets(team.getId()).forEach(get -> game.addStaticGoal(get.getId(), get.getDisplay()));
        });

        game.getPlaying().forEach(player -> {
            if (target.getStage() == DestroyTarget.Stage.END) {
                FunEffectsUtil.playSound(player.getPlayer(), Sound.ORB_PICKUP);
                Common.sendAnimatedActionBar(player, Msg.locale(player, "destroy.done", gamePlayer.getPlayerColor(), target.getOwnerName(), target.getName()));
            }
            else if (player.getTeam() == gamePlayer.getTeam()) {
                Common.sendAnimatedActionBar(player, Msg.locale(player, "destroy.damage", gamePlayer.getPlayerColor(), target.getOwnerName(), target.getName()));
            }
        });

        // Tokens
        if (MapNodesPlugin.getInst().isDebug()) {
            String tokens = MessageUtil.replaceColors("&7(DEBUG) &b+10 &6tokens ");
            gamePlayer.sendMessage(tokens);
            MapNodesPlugin.debug(gamePlayer.getPlayerColor() + " " + tokens);
            ((NodePlayer) gamePlayer).getCreditsMultiplier().incrementAndGet();
        }
        else {
            gamePlayer.sendMessage(MessageUtil.replaceColors("&b+10 &6tokens"));
            MapNodesPlugin.getInst().getApi().addTokens(gamePlayer, 10);
            ((NodePlayer) gamePlayer).getCreditsMultiplier().incrementAndGet();
        }

        SchedulerUtil.runSync(this::shouldWin);
    }

    /** Check is all targets are done */
    private void shouldWin() {
        for (GameTeam team : game.getPlayingTeams().collect(Collectors.toList())) {
            List<DestroyTarget> targets = gameModeConfig.getChallengerTargets(team.getId());
            int count = targets.size();

            for (DestroyTarget target : targets) {
                if (target.getStage() == DestroyTarget.Stage.END) {
                    count--;
                }
            }

            if (count <= 0) {
                new GameTeamWinEvent(game, team).call();
                break;
            }
        }
    }
}
