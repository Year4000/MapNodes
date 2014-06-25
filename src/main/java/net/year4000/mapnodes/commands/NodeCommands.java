package net.year4000.mapnodes.commands;

import com.ewized.utilities.bukkit.util.MessageUtil;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import net.year4000.mapnodes.configs.MainConfig;
import net.year4000.mapnodes.configs.Messages;
import net.year4000.mapnodes.game.GameManager;
import net.year4000.mapnodes.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public final class NodeCommands {
    @Command(
        aliases = {"add"},
        usage = "[map name]",
        desc = "Add a map to the node.",
        min = 1,
        max = 1
    )
    public static void add(CommandContext args, CommandSender sender) throws CommandException {
        WorldManager wm = WorldManager.get();
        OfflinePlayer player = Bukkit.getPlayer(sender.getName());
        String locale = player == null ? "messages" : player.getPlayer().getLocale();

        if (wm.getWorld(args.getString(0)) == null)
            throw new CommandException(Messages.get(locale, "command-node-noWorld"));

        World map = wm.createWorld(wm.copyWorld(args.getString(0)));

        if (!wm.loadMap(map))
            throw new CommandException(Messages.get(locale, "command-node-notLoaded"));

        sender.sendMessage(MessageUtil.message(
            Messages.get(locale, "command-node-loaded"),
            wm.nextGame().getMap().getName()
        ));
    }

    @Command(
        aliases = {"remove"},
        usage = "[map index]",
        desc = "Remove a map from the node.",
        min = 1,
        max = 1
    )
    public static void remove(CommandContext args, CommandSender sender) throws CommandException {
        OfflinePlayer player = Bukkit.getPlayer(sender.getName());
        int index = args.getInteger(0)-1;
        String locale = player == null ? "messages" : player.getPlayer().getLocale();

        if (index >= WorldManager.get().getGames().size())
            throw new CommandException(Messages.get(locale, "command-node-out"));

        GameManager gm = (GameManager)WorldManager.get().getGames().toArray()[index];
        WorldManager.get().unLoadMap(gm.getWorld());
        WorldManager.get().getGames().remove(gm);

        sender.sendMessage(MessageUtil.message(
            Messages.get(locale, "command-node-removed"),
            gm.getMap().getName()
        ));
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
        Player player = Bukkit.getPlayer(sender.getName());
        String locale = player == null ? "messages" : player.getLocale();

        try {
            sender.sendMessage(Messages.get(locale, "command-node-generate-start"));
            long startTime = System.currentTimeMillis();

            for (String map : wm.copyWorlds(maps)) {
                wm.loadMap(wm.createWorld(map));
            }

            long time = (System.currentTimeMillis()-startTime)/100;
            sender.sendMessage(String.format(Messages.get(locale, "command-node-generate-end"), time));
        } catch (NullPointerException e) {
            throw new CommandException(e.getMessage());
        }
    }
}
