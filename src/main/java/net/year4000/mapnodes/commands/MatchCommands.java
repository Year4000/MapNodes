package net.year4000.mapnodes.commands;

import net.year4000.mapnodes.configs.Messages;
import net.year4000.mapnodes.game.GameStage;
import net.year4000.mapnodes.world.WorldManager;
import net.year4000.utilities.bukkit.commands.Command;
import net.year4000.utilities.bukkit.commands.CommandContext;
import net.year4000.utilities.bukkit.commands.CommandException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

@SuppressWarnings("unused")
public final class MatchCommands {
    @Command(
        aliases = {"start", "begin"},
        desc = "Start the match!",
        max = 0
    )
    public static void start(CommandContext args, CommandSender sender) throws CommandException {
        OfflinePlayer player = Bukkit.getPlayer(sender.getName());
        String locale = player == null ? "messages" : player.getPlayer().getLocale();

        if (GameStage.isEndGame() || GameStage.isPlaying())
            throw new CommandException(Messages.get(locale, "command-match-start"));

        WorldManager.get().getCurrentGame().setManStart(true);
        sender.sendMessage(Messages.get(locale, "command-match-start-notice"));
        WorldManager.get().getCurrentGame().startMatch();
    }

    @Command(
        aliases = {"end", "stop", "finish"},
        desc = "Stop the match.",
        max = 0
    )
    public static void stop(CommandContext args, CommandSender sender) throws CommandException {
        OfflinePlayer player = Bukkit.getPlayer(sender.getName());
        String locale = player == null ? "messages" : player.getPlayer().getLocale();

        if (GameStage.isPreGame() || GameStage.isEndGame())
            throw new CommandException(Messages.get(locale, "command-match-stop"));

        sender.sendMessage(Messages.get(locale, "command-match-stop-notice"));
        WorldManager.get().getCurrentGame().stopMatch();
    }
}
