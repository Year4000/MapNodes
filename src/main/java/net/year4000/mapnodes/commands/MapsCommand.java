package net.year4000.mapnodes.commands;

import net.year4000.mapnodes.MapNodes;
import net.year4000.mapnodes.game.GameManager;
import net.year4000.mapnodes.world.WorldManager;
import net.year4000.utilities.bukkit.MessageUtil;
import net.year4000.utilities.bukkit.commands.*;
import net.year4000.utilities.bukkit.pagination.SimplePaginatedResult;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public final class MapsCommand {
    public MapsCommand() {
        new CommandsManagerRegistration(
            MapNodes.getInst(),
            MapNodes.getInst().getCommands()
        ).register(MapsCommand.class);
    }

    @Command(
        aliases = {"maps", "cycle", "rotation", "queue"},
        usage = "(page)",
        desc = "Show the maps that are loaded to the server.",
        max = 1
    )
    public static void maps(CommandContext args, CommandSender sender) throws CommandException {
        final int MAXPERPAGE = 8;
        //Create a new list that shows current and queued maps
        List<GameManager> maps = new ArrayList<>();
        maps.add(WorldManager.get().getCurrentGame());
        maps.addAll(WorldManager.get().getGames());

        new SimplePaginatedResult<GameManager>("Queued Maps", MAXPERPAGE) {
            @Override
            public String formatHeader(int page, int maxPages) {
                return MessageUtil.message(
                    "&7&m**********&a &l%s &2(page &a%s&2/&a%s&2) &7&m**********",
                    header,
                    page,
                    maxPages
                );
            }

            @Override
            public String format(GameManager game, int index) {
                if (index == 0) {
                    return MessageUtil.message(
                        "&6Current&7: &a%s &2version &a%s &2by &a%s",
                        game.getMap().getName(),
                        game.getMap().getVersion(),
                        game.getMap().getAuthors().get(0)
                    );
                }
                return MessageUtil.message(
                    "&7%s &a%s &2version &a%s &2by &a%s",
                    index+1,
                    game.getMap().getName(),
                    game.getMap().getVersion(),
                    game.getMap().getAuthors().get(0)
                );
            }
        }.display(
            new BukkitWrappedCommandSender(sender),
            maps,
            args.argsLength() == 1 ? args.getInteger(0) : 1
        );
    }

    @Command(
        aliases = {"map", "current"},
        desc = "Show the next map.",
        max = 0
    )
    public static void current(CommandContext args, CommandSender sender) throws CommandException {
        sender.sendMessage(map("&6Current Game:", WorldManager.get().getCurrentGame()));
    }

    @Command(
        aliases = {"next", "nextmap"},
        desc = "Show the next map.",
        max = 0
    )
    public static void next(CommandContext args, CommandSender sender) throws CommandException {
        sender.sendMessage(map("&6Next Game:", WorldManager.get().getNextGame()));
    }

    /** The string for the map */
    private static String map(String prefix, GameManager game) {
        return MessageUtil.replaceColors(String.format(
            "%s &a%s &2version &a%s &2by &a%s",
            prefix,
            game.getMap().getName(),
            game.getMap().getVersion(),
            game.getMap().getAuthors().get(0)
        ));
    }
}
