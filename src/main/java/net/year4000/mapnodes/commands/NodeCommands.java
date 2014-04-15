package net.year4000.mapnodes.commands;

import com.ewized.utilities.bukkit.util.MessageUtil;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import net.year4000.mapnodes.MapNodes;
import net.year4000.mapnodes.configs.MainConfig;
import net.year4000.mapnodes.configs.Messages;
import net.year4000.mapnodes.game.GameManager;
import net.year4000.mapnodes.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

@SuppressWarnings("unused")
public final class NodeCommands {
    @Command(
        aliases = {"add"},
        usage = "[map name] (index)",
        desc = "Add a map to the node.",
        min = 1,
        max = 2
    )
    public static void add(CommandContext args, CommandSender sender) throws CommandException {
        WorldManager wm = WorldManager.get();
        boolean hasIndex = args.argsLength() > 2;

        if (wm.getWorld(args.getString(0)) == null)
            throw new CommandException(Messages.get("command.node.noWorld"));

        if (hasIndex && args.getInteger(1)-1 < WorldManager.get().getCurrentIndex())
            throw new CommandException(Messages.get("command.node.future"));

        if (hasIndex && args.getInteger(1)-1 > WorldManager.get().getGames().size())
            throw new CommandException(Messages.get("command.node.out"));

        World map = wm.createWorld(wm.copyWorld(args.getString(0)));

        Integer position = hasIndex ? args.getInteger(1) : wm.getGames().size();

        if (!wm.loadMap(map, position))
            throw new CommandException(Messages.get("command.node.notLoaded"));

        sender.sendMessage(MessageUtil.replaceColors(String.format(
            Messages.get("command.node.loaded"),
            wm.getGames().get(position).getMap().getName()
        )));
    }

    @Command(
        aliases = {"remove"},
        usage = "[map index]",
        desc = "Remove a map from the node.",
        min = 1,
        max = 1
    )
    public static void remove(CommandContext args, CommandSender sender) throws CommandException {
        if (args.getInteger(0)-1 <= WorldManager.get().getCurrentIndex())
            throw new CommandException(Messages.get("command.node.future"));

        if (args.getInteger(0)-1 >= WorldManager.get().getGames().size())
            throw new CommandException(Messages.get("command.node.out"));

        GameManager gm = WorldManager.get().getGames().remove(args.getInteger(0) - 1);
        WorldManager.get().unLoadMap(gm.getWorld());

        sender.sendMessage(MessageUtil.replaceColors(String.format(
            Messages.get("command.node.removed"),
            gm.getMap().getName()
        )));
    }

    @Command(
        aliases = {"move"},
        usage = "[map index] [nex index]",
        desc = "Move one map node to the other.",
        min = 2,
        max = 2
    )
    public static void move(CommandContext args, CommandSender sender) throws CommandException {
        if (args.getInteger(0)-1 <= WorldManager.get().getCurrentIndex())
            throw new CommandException(Messages.get("command.node.future"));

        if (args.getInteger(0)-1 >= WorldManager.get().getGames().size())
            throw new CommandException(Messages.get("command.node.out"));

        if (args.getInteger(1)-1 < WorldManager.get().getCurrentIndex())
            throw new CommandException(Messages.get("command.node.future"));

        if (args.getInteger(1)-1 >= WorldManager.get().getGames().size())
            throw new CommandException(Messages.get("command.node.out"));

        GameManager gm = WorldManager.get().getGames().remove(args.getInteger(0) - 1);
        WorldManager.get().getGames().add(args.getInteger(1), gm);

        sender.sendMessage(MessageUtil.replaceColors(String.format(
            Messages.get("command.node.moved"),
            gm.getMap().getName()
        )));
    }

    @Command(
        aliases = {"generate"},
        usage = "(count)",
        desc = "Generate random maps to the node.",
        min = 0,
        max = 1
    )
    public static void generate(CommandContext args, CommandSender sender) throws CommandException {
        int maps = args.argsLength() < 1 ? MainConfig.get().getMaxLoadMaps() : args.getInteger(0);
        WorldManager wm = WorldManager.get();

        try {
            sender.sendMessage(Messages.get("command.node.generate.start"));
            long startTime = System.currentTimeMillis();

            for (String map : wm.copyWorlds(maps)) {
                wm.loadMap(wm.createWorld(map));
            }

            long time = (System.currentTimeMillis()-startTime)/100;
            sender.sendMessage(String.format(Messages.get("command.node.generate.end"), time));
        } catch (NullPointerException e) {
            throw new CommandException(e.getMessage());
        }
    }
}
