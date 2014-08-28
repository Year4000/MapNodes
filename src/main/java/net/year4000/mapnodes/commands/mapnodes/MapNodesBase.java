package net.year4000.mapnodes.commands.mapnodes;

import net.year4000.utilities.bukkit.commands.Command;
import net.year4000.utilities.bukkit.commands.CommandContext;
import net.year4000.utilities.bukkit.commands.CommandException;
import net.year4000.utilities.bukkit.commands.NestedCommand;
import org.bukkit.command.CommandSender;

public final class MapNodesBase {
    @Command(
        aliases = {"mapnodes"},
        desc = "Command to view info about MapNodes"
    )
    @NestedCommand({MapNodesSub.class})
    public static void node(CommandContext args, CommandSender sender) throws CommandException {
        // Empty used as a base command
    }
}
