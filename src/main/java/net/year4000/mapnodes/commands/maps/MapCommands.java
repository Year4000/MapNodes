package net.year4000.mapnodes.commands.maps;

import com.ewized.utilities.bukkit.util.MessageUtil;
import com.ewized.utilities.core.util.ChatColor;
import com.sk89q.bukkit.util.BukkitWrappedCommandSender;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.pagination.SimplePaginatedResult;
import net.year4000.mapnodes.NodeFactory;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.api.game.GameMap;
import net.year4000.mapnodes.game.Node;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Common;
import org.bukkit.command.CommandSender;

public class MapCommands {
    @Command(
        aliases = {"maps", "cycle", "rotation", "queue"},
        usage = "(page)",
        desc = "Show the maps that are loaded to the server.",
        max = 1
    )
    public static void maps(CommandContext args, CommandSender sender) throws CommandException {
        final int MAX_PER_PAGE = 8;

        new SimplePaginatedResult<Node>(null, MAX_PER_PAGE) {
            @Override
            public String formatHeader(int page, int maxPages) {
                return MessageUtil.message(Msg.locale(sender, "cmd.maps.header"), page, maxPages);
            }

            @Override
            public String format(Node node, int index) {
                GameManager game = node.getMatch().getGame();

                if (index == 0) {
                    return MessageUtil.message(
                        Msg.locale(sender, "cmd.maps.current"),
                        game.getMap().getName(),
                        Common.formatSeperators(game.getMap().getVersion(), ChatColor.GREEN, ChatColor.DARK_GRAY),
                        author(sender, game.getMap())
                    );
                }
                return MessageUtil.message(
                    Msg.locale(sender, "cmd.maps.queued"),
                    index,
                    game.getMap().getName(),
                    Common.formatSeperators(game.getMap().getVersion(), ChatColor.GREEN, ChatColor.DARK_GRAY),
                    author(sender, game.getMap())
                );
            }
        }.display(
            new BukkitWrappedCommandSender(sender),
            NodeFactory.get().getAllGames(),
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

        sender.sendMessage(map(sender, "cmd.next", (GameManager)NodeFactory.get().peekNextQueued().getMatch()));
    }

    /** The string for the map */
    private static String map(CommandSender sender, String format, GameManager game) {
        return MessageUtil.replaceColors(String.format(
            Msg.locale(sender, format),
            game.getMap().getName(),
            Common.formatSeperators(game.getMap().getVersion(), ChatColor.GREEN, ChatColor.DARK_GRAY),
            author(sender, game.getMap())
        ));
    }

    /** Fancy authors display */
    private static String author(CommandSender sender, GameMap map) {
        if (map.hasOtherAuthors()) {
            int size = map.getOtherAuthors().size();
            String authors = Msg.locale(sender, "map.authors");

            return MessageUtil.message(
                size == 1 ? authors.substring(0, authors.length() - 1) : authors,
                map.getMainAuthor(),
                size
            );
        }

        return MessageUtil.message(Msg.locale(sender, "map.author"), map.getMainAuthor());
    }
}
