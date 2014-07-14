package net.year4000.mapnodes.commands.mapnodes;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.NestedCommand;
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
