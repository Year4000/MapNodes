/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.clocks;

import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.api.game.GameMap;
import net.year4000.mapnodes.game.NodeGame;
import net.year4000.mapnodes.game.NodeStage;
import net.year4000.mapnodes.messages.Message;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Common;
import net.year4000.mapnodes.utils.MathUtil;
import net.year4000.mapnodes.utils.PacketHacks;
import net.year4000.mapnodes.utils.TimeUtil;
import net.year4000.utilities.bukkit.FunEffectsUtil;
import net.year4000.utilities.bukkit.bossbar.BossBar;
import org.bukkit.Sound;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static net.year4000.mapnodes.utils.MathUtil.percent;
import static net.year4000.mapnodes.utils.MathUtil.ticks;

public class StartGame extends Clocker {
    private static boolean running = false;
    private Integer[] ticks = {
        ticks(5),
        ticks(4),
        ticks(3),
        ticks(2),
        ticks(1)
    };

    public StartGame() {
        this(30);
    }

    public StartGame(int time) {
        super(MathUtil.ticks(MapNodesPlugin.getInst().getLog().isDebug() ? 10 : time));
    }

    public void runFirst(int position) {
        if (running) {
            getClock().task.cancel();
        }

        running = true;
        NodeGame game = (NodeGame) MapNodes.getCurrentGame();
        game.setStage(NodeStage.STARTING);
        game.setStartClock(this);
        GameMap map = game.getMap();

        MapNodesPlugin.log(Msg.util("clocks.start.first", map.getName(), (new TimeUtil(sec(position) - 1, TimeUnit.SECONDS)).rawOutput()));
        game.getPlayers().parallel().forEach(player -> {
            FunEffectsUtil.playSound(player.getPlayer(), Sound.ORB_PICKUP);
        });
    }

    public void runTock(int position) {
        GameManager game = MapNodes.getCurrentGame();
        int pos = sec(position);
        String color = Common.chatColorNumber(pos, sec(getTime()));
        String time = color + (new TimeUtil(pos, TimeUnit.SECONDS)).prettyOutput("&7:" + color);

        MapNodes.getCurrentGame().getPlayers().forEach(player -> {
            // Classes need VIP to use
            /*if (game.getClasses().size() > 0 && position % 20 == 0) {
                if (player.getClassKit() == null) {
                    List<String> list = new ArrayList<>(game.getClasses().keySet());
                    player.setClassKit(game.getClasses().get(list.get(new Random().nextInt(list.size()))));
                }
            }*/

            if (Arrays.asList(ticks).contains(position)) {
                FunEffectsUtil.playSound(player.getPlayer(), Sound.NOTE_PLING);
            }

            PacketHacks.countTitle(player.getPlayer(), Msg.locale(player, "clocks.start.tock.new"), time, percent(getTime(), position));
        });
    }

    public void runLast(int position) {
        MapNodesPlugin.log(Msg.locale(Message.DEFAULT_LOCALE, "clocks.start.last"));

        MapNodes.getCurrentGame().getPlayers().forEach(player -> {
            FunEffectsUtil.playSound(player.getPlayer(), Sound.NOTE_BASS);

            PacketHacks.setTitle(player.getPlayer(), Msg.locale(player, "clocks.start.last.new"), "");

            BossBar.removeBar(player.getPlayer());
        });

        ((NodeGame) MapNodes.getCurrentGame()).start();
        running = false;
    }
}