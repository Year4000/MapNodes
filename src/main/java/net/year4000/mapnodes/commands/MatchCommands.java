package net.year4000.mapnodes.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import net.year4000.mapnodes.configs.Messages;
import net.year4000.mapnodes.game.GameStage;
import net.year4000.mapnodes.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public final class MatchCommands {
    @Command(
        aliases = {"start", "begin"},
        desc = "Start the match!",
        max = 0
    )
    public static void start(CommandContext args, CommandSender sender) throws CommandException {
        Player player = Bukkit.getPlayer(sender.getName()).getPlayer();

        if (GameStage.isEndGame() || GameStage.isPlaying())
            throw new CommandException(Messages.get(player.getLocale(), "command-match-start"));

        WorldManager.get().getCurrentGame().setManStart(true);
        sender.sendMessage(Messages.get(player.getLocale(), "command-match-start-notice"));
        WorldManager.get().getCurrentGame().startMatch();
    }

    @Command(
        aliases = {"end", "stop", "finish"},
        desc = "Stop the match.",
        max = 0
    )
    public static void stop(CommandContext args, CommandSender sender) throws CommandException {
        Player player = Bukkit.getPlayer(sender.getName()).getPlayer();

        if (GameStage.isPreGame() || GameStage.isEndGame())
            throw new CommandException(Messages.get(player.getLocale(), "command-match-stop"));

        sender.sendMessage(Messages.get(player.getLocale(), "command-match-stop-notice"));
        WorldManager.get().getCurrentGame().stopMatch();
    }
}
