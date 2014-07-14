package net.year4000.mapnodes.commands.match;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.clocks.StartGame;
import net.year4000.mapnodes.game.NodeGame;
import org.bukkit.command.CommandSender;

import static com.google.common.base.Preconditions.checkArgument;

public final class MatchSub {
    @Command(
        aliases = {"start"},
        flags = "n",
        max = 1,
        desc = "Start the match."
    )
    public static void start(CommandContext args, CommandSender sender) throws CommandException {
        // checks
        try {
            checkArgument(!MapNodes.getCurrentGame().getStage().isStarting());
            checkArgument(!MapNodes.getCurrentGame().getStage().isPlaying());
            checkArgument(!MapNodes.getCurrentGame().getStage().isEndGame());
        } catch (IllegalArgumentException e) {
            throw new CommandException(e.getMessage());
        }

        // Start the game now
        if (args.hasFlag('n')) {
            ((NodeGame) MapNodes.getCurrentGame()).start();
        }
        // Start the game with the clock
        else if (args.argsLength() > 0){
            new StartGame(args.getInteger(0)).run();
        }
        else {
            new StartGame().run();
        }
    }

    @Command(
        aliases = {"stop", "skip"},
        max = 1,
        desc = "Stop the match."
    )
    public static void stop(CommandContext args, CommandSender sender) throws CommandException {

        if (!args.getCommand().equalsIgnoreCase("skip")) {
            try {
                checkArgument(!MapNodes.getCurrentGame().getStage().isPreGame());
                checkArgument(!MapNodes.getCurrentGame().getStage().isEndGame());
            } catch (IllegalArgumentException e) {
                throw new CommandException(e.getMessage());
            }
        }

        // Start the game with the clock
        if (args.argsLength() > 0){
            ((NodeGame) MapNodes.getCurrentGame()).stop(args.getInteger(0));
        }
        else {
            ((NodeGame) MapNodes.getCurrentGame()).stop();
        }
    }
}
