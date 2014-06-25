package net.year4000.mapnodes.commands;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.*;
import net.year4000.mapnodes.MapNodes;
import org.bukkit.command.CommandSender;

public final class NodeBaseCommand {
    public NodeBaseCommand() {
        new CommandsManagerRegistration(
            MapNodes.getInst(),
            MapNodes.getInst().getCommands()
        ).register(NodeBaseCommand.class);
    }

    @Command(
        aliases = {"node", "mapnodes"},
        desc = "Nodes base command to manage the maps."
    )
    @CommandPermissions({"mapnodes.admin", "mapnodes.*"})
    @NestedCommand({NodeCommands.class})
    public static void node(CommandContext args, CommandSender sender) throws CommandException {}
}