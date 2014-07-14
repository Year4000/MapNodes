package net.year4000.mapnodes.commands.misc;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.game.NodeGame;
import net.year4000.mapnodes.game.NodePlayer;
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
        NodeGame ng = (NodeGame) MapNodes.getCurrentGame();

        try {
            ng.quit((Player)sender);
            ng.join((Player)sender);

        } catch (Exception e) {
            throw new CommandException(e.getMessage());
        }
    }
}
