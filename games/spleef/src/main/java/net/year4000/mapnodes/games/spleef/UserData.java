/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.games.spleef;

import lombok.Getter;
import lombok.ToString;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.SchedulerUtil;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

@Getter
@ToString
public class UserData {
    private final GamePlayer gamePlayer;
    private final Player player;
    private Optional<BukkitTask> speed = Optional.empty();

    public UserData(GamePlayer player) {
        this.gamePlayer = checkNotNull(player);
        this.player = gamePlayer.getPlayer();
    }

    /** Add the expire speed message and cancel old one */
    public void addSpeed(String message, String power, int ticks) {
        speed.ifPresent(BukkitTask::cancel);
        String msg = Msg.locale(gamePlayer, message, power);
        speed = Optional.of(SchedulerUtil.runAsync(() -> player.sendMessage(msg), ticks));
    }
}
