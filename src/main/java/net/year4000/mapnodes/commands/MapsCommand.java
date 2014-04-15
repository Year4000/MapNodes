package net.year4000.mapnodes.commands;

import com.ewized.utilities.bukkit.util.MessageUtil;
import com.sk89q.bukkit.util.BukkitWrappedCommandSender;
import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.pagination.SimplePaginatedResult;
import net.year4000.mapnodes.MapNodes;
import net.year4000.mapnodes.game.GameManager;
import net.year4000.mapnodes.world.WorldManager;
import org.bukkit.command.CommandSender;

@SuppressWarnings("unused")
public final class MapsCommand {
    public MapsCommand() {
        new CommandsManagerRegistration(
            MapNodes.getInst(),
            MapNodes.getInst().getCommands()
        ).register(MapsCommand.class);
    }

    @Command(
        aliases = {"maps", "cycle", "rotation"},
        usage = "(page)",
        desc = "Show the maps that are loaded to the server.",
        max = 1
    )
    public static void maps(CommandContext args, CommandSender sender) throws CommandException {
        final int MAXPERPAGE = 8;
        new SimplePaginatedResult<GameManager>("Loaded Maps", MAXPERPAGE) {
            @Override
            public String formatHeader(int page, int maxPages) {
                return MessageUtil.replaceColors(String.format(
                    "&7------- &a&l%s &2(page &a%s&2/&a%s&2) &7-------",
                    header,
                    page,
                    maxPages
                ));
            }

            @Override
            public String format(GameManager game, int index) {
                String prefix = index == WorldManager.get().getCurrentIndex() ? "&3&l": "";

                return MessageUtil.replaceColors(String.format(
                    "&7%s%s &a%s%s &2%sversion &a%s%s &2%sby &a%s%s",
                    prefix,
                    index+1,
                    prefix,
                    game.getMap().getName(),
                    prefix,
                    prefix,
                    game.getMap().getVersion(),
                    prefix,
                    prefix,
                    game.getMap().getAuthors().get(0)
                ));
            }
        }.display(
            new BukkitWrappedCommandSender(sender),
            WorldManager.get().getGames(),
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

    @Command(
            aliases = {"last", "lastmap"},
            desc = "Show the last map.",
            max = 0
    )
    public static void last(CommandContext args, CommandSender sender) throws CommandException {
        sender.sendMessage(map("&6Last Game:", WorldManager.get().getLastGame()));
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
