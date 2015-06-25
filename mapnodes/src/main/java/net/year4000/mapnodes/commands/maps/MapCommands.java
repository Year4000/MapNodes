/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.commands.maps;

import com.sk89q.bukkit.util.BukkitWrappedCommandSender;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.pagination.SimplePaginatedResult;
import net.year4000.mapnodes.NodeFactory;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.game.Node;
import net.year4000.mapnodes.map.MapFactory;
import net.year4000.mapnodes.map.MapObject;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Common;
import net.year4000.utilities.ChatColor;
import net.year4000.utilities.MessageUtil;
import org.bukkit.command.CommandSender;

public class MapCommands {
    @Command(
        aliases = {"maps", "cycle", "rotation", "queue"},
        usage = "(page)",
        desc = "Show the maps that are loaded to the server queue.",
        max = 1
    )
    public static void maps(CommandContext args, CommandSender sender) throws CommandException {
        final int MAX_PER_PAGE = 8;

        new SimplePaginatedResult<Node>(null, MAX_PER_PAGE) {
            @Override
            public String formatHeader(int page, int maxPages) {
                return Msg.locale(sender, "cmd.maps.header", String.valueOf(page), String.valueOf(maxPages));
            }

            @Override
            public String format(Node node, int index) {
                GameManager game = node.getGame();

                // todo only show map name as maps update right before the game starts.
                if (index == 0) {
                    return Msg.locale(sender, "cmd.maps.current",
                        game.getMap().getName(),
                        Common.formatSeparators(game.getMap().getVersion(), ChatColor.GREEN, ChatColor.DARK_GRAY),
                        game.getMap().author(sender)
                    );
                }
                return Msg.locale(sender, "cmd.maps.queued",
                    String.valueOf(index),
                    game.getMap().getName(),
                    Common.formatSeparators(game.getMap().getVersion(), ChatColor.GREEN, ChatColor.DARK_GRAY),
                    game.getMap().author(sender)
                );
            }
        }.display(
            new BukkitWrappedCommandSender(sender),
            NodeFactory.get().getAllGames(),
            args.argsLength() == 1 ? args.getInteger(0) : 1
        );
    }

    @Command(
        aliases = {"loaded"},
        usage = "(page)",
        desc = "Show the maps that are loaded to the server.",
        max = 1
    )
    public static void loaded(CommandContext args, CommandSender sender) throws CommandException {
        final int MAX_PER_PAGE = 8;

        new SimplePaginatedResult<MapObject>(null, MAX_PER_PAGE) {
            @Override
            public String formatHeader(int page, int maxPages) {
                return Msg.locale(sender, "cmd.maps.header", String.valueOf(page), String.valueOf(maxPages));
            }

            @Override
            public String format(MapObject folder, int index) {

                return MessageUtil.replaceColors("&7" + (index + 1) + " &a" + folder.toString().replaceAll("(/|\\(|\\))", "&7$1&a"));
            }
        }.display(
            new BukkitWrappedCommandSender(sender),
            MapFactory.getFolders().values(),
            args.argsLength() == 1 ? args.getInteger(0) : 1
        );
    }

    @Command(
        aliases = {"map", "current"},
        desc = "Show the next map.",
        max = 0
    )
    public static void current(CommandContext args, CommandSender sender) throws CommandException {
        sender.sendMessage(map(sender, "cmd.current", MapNodes.getCurrentGame()));
    }

    @Command(
        aliases = {"next", "nextmap"},
        desc = "Show the next map.",
        max = 0
    )
    public static void next(CommandContext args, CommandSender sender) throws CommandException {
        if (!NodeFactory.get().isQueuedGames()) {
            throw new CommandException(Msg.locale(sender, "cmd.next.none"));
        }

        sender.sendMessage(map(sender, "cmd.next", (GameManager) NodeFactory.get().peekNextQueued().getGame()));
    }

    /** The string for the map */
    private static String map(CommandSender sender, String format, GameManager game) {
        // todo only show map name as maps update right before the game starts.
        return Msg.locale(
            sender,
            format,
            game.getMap().getName(),
            Common.formatSeparators(game.getMap().getVersion(), ChatColor.GREEN, ChatColor.DARK_GRAY),
            game.getMap().author(sender)
        );
    }
}
