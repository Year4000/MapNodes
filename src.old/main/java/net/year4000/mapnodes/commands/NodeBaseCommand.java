package net.year4000.mapnodes.commands;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.*;
import net.year4000.mapnodes.MapNodesPlugin;
import org.bukkit.command.CommandSender;

public final class NodeBaseCommand {
    public NodeBaseCommand() {
        new CommandsManagerRegistration(
            MapNodesPlugin.getInst(),
            MapNodesPlugin.getInst().getCommands()
        ).register(NodeBaseCommand.class);
    }

    @Command(
        aliases = {"node", "mapnodes"},
        desc = "Nodes base command to manage the maps."
    )
    @CommandPermissions({"mapnodes.admin", "mapnodes.*"})
    @NestedCommand(value = {NodeCommands.class}, executeBody = true)
    public static void node(CommandContext args, CommandSender sender) throws CommandException {

    }
}