package net.year4000.mapnodes.commands.misc;

import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.game.NodePlayer;
import net.year4000.utilities.bukkit.commands.Command;
import net.year4000.utilities.bukkit.commands.CommandContext;
import net.year4000.utilities.bukkit.commands.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class MenuCommands {
    @Command(
        aliases = {"join", "team"},
        usage = "[team]",
        max = 1,
        min = 1,
        desc = "Join the team."
    )
    public static void team(CommandContext args, CommandSender sender) throws CommandException {
        GameManager gm = MapNodes.getCurrentGame();

        try {
            ((NodePlayer) gm.getPlayer((Player) sender)).joinTeam(gm.getTeams().get(args.getString(0)));
        } catch (Exception e) {
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
            ((NodePlayer) gm.getPlayer((Player) sender)).joinTeam(gm.getTeams().get(args.getString(0)));
        } catch (Exception e) {
            throw new CommandException(e.getMessage());
        }
    }

    @Command(
        aliases = {"spectator", "spec"},
        desc = "Spectate the game."
    )
    public static void spectator(CommandContext args, CommandSender sender) throws CommandException {
        try {
            // todo check if things before setting to spectator
            ((NodePlayer) MapNodes.getCurrentGame().getPlayer((Player) sender)).join();
        } catch (Exception e) {
            throw new CommandException(e.getMessage());
        }
    }
}
