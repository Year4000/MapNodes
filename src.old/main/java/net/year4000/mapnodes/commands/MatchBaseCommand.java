package net.year4000.mapnodes.commands;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.*;
import net.year4000.mapnodes.MapNodesPlugin;
import org.bukkit.command.CommandSender;

public final class MatchBaseCommand {
    public MatchBaseCommand() {
        new CommandsManagerRegistration(
            MapNodesPlugin.getInst(),
            MapNodesPlugin.getInst().getCommands()
        ).register(MatchBaseCommand.class);
    }

    @Command(
        aliases = {"match"},
        desc = "Effect the match at hand."
    )
    @CommandPermissions({"mapnodes.admin", "mapnodes.*"})
    @NestedCommand({MatchCommands.class})
    public static void match(CommandContext args, CommandSender sender) throws CommandException {}
}
