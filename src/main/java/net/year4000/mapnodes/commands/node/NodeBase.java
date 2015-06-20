/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.commands.node;

import org.bukkit.command.CommandSender;

public final class NodeBase {
    @Command(
        aliases = {"node"},
        desc = "Manage the game nodes in the queue"
    )
    @CommandPermissions({"omega", "delta", "mapnodes.admin", "mapnodes.*"})
    @NestedCommand({NodeSub.class})
    public static void node(CommandContext args, CommandSender sender) throws CommandException {
        // Empty used as a base command
    }
}
