package net.year4000.mapnodes.commands.match;

import com.sk89q.minecraft.util.commands.*;
import org.bukkit.command.CommandSender;

public final class MatchBase {
    @Command(
        aliases = {"match"},
        desc = "Command to view info about MapNodes"
    )
    @CommandPermissions({"mapnodes.admin", "mapnodes.*"})
    @NestedCommand({MatchSub.class})
    public static void match(CommandContext args, CommandSender sender) throws CommandException {
        // Empty used as a base command
    }
}
