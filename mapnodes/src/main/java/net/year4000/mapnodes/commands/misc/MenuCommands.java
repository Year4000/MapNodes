/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.commands.misc;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.game.NodeGame;
import net.year4000.mapnodes.game.NodePlayer;
import net.year4000.mapnodes.messages.Msg;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.google.common.base.Preconditions.checkArgument;

public final class MenuCommands {
    @Command(
        aliases = {"join", "team"},
        usage = "[team]",
        max = 1,
        min = 1,
        desc = "Join the team."
    )
    public static void team(CommandContext args, CommandSender sender) throws CommandException {
        NodeGame game = (NodeGame) MapNodes.getCurrentGame();


        try {
            NodePlayer player = ((NodePlayer) game.getPlayer((Player) sender));

            checkArgument(!game.getStage().isEndGame(), Msg.locale(player, "team.menu.not_now"));

            if (player.isPlaying()) {
                throw new CommandException(Msg.locale(sender, "team.join.only_spectator") + "\n" + Msg.locale(sender, "team.select.non_vip_url"));
            }

            player.joinTeam(game.checkAndGetTeam(player, args.getString(0)));
        }
        catch (Exception e) {
            throw new CommandException(e.getMessage());
        }
    }

    @Command(
        aliases = {"class", "kit"},
        usage = "[class]",
        max = 1,
        min = 1,
        desc = "Pick a class."
    )
    public static void clazz(CommandContext args, CommandSender sender) throws CommandException {
        GameManager gm = MapNodes.getCurrentGame();

        try {
        }
        catch (Exception e) {
            throw new CommandException(e.getMessage());
        }
    }

    @Command(
        aliases = {"spectator", "spec"},
        desc = "Spectate the game."
    )
    public static void spectator(CommandContext args, CommandSender sender) throws CommandException {
        try {
            ((NodePlayer) MapNodes.getCurrentGame().getPlayer((Player) sender)).join();
        }
        catch (Exception e) {
            throw new CommandException(e.getMessage());
        }
    }
}
