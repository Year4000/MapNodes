package net.year4000.mapnodes.commands.node;

import com.sk89q.minecraft.util.commands.*;
import org.bukkit.command.CommandSender;

public final class NodeBase {
    @Command(
        aliases = {"node"},
        desc = "Manage the game nodes in the queue"
    )
    @CommandPermissions({"mapnodes.admin", "mapnodes.*"})
    @NestedCommand({NodeSub.class})
    public static void node(CommandContext args, CommandSender sender) throws CommandException {
        // Empty used as a base command
    }
}
