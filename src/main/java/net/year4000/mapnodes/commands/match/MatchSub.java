package net.year4000.mapnodes.commands.match;

import com.google.common.annotations.Beta;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.events.game.GameWinEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerWinEvent;
import net.year4000.mapnodes.api.events.team.GameTeamWinEvent;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.GameTeam;
import net.year4000.mapnodes.clocks.StartGame;
import net.year4000.mapnodes.game.NodeGame;
import net.year4000.utilities.bukkit.commands.Command;
import net.year4000.utilities.bukkit.commands.CommandContext;
import net.year4000.utilities.bukkit.commands.CommandException;
import org.bukkit.command.CommandSender;

import java.util.stream.Collectors;

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
        }
        catch (IllegalArgumentException e) {
            throw new CommandException(e.getMessage());
        }

        // Start the game now
        if (args.hasFlag('n')) {
            ((NodeGame) MapNodes.getCurrentGame()).start();
        }
        // Start the game with the clock
        else if (args.argsLength() > 0) {
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
            }
            catch (IllegalArgumentException e) {
                throw new CommandException(e.getMessage());
            }
        }

        // Stop the game with the clock
        if (args.argsLength() > 0) {
            ((NodeGame) MapNodes.getCurrentGame()).stop(args.getInteger(0));
        }
        else {
            ((NodeGame) MapNodes.getCurrentGame()).stop();
        }
    }

    @Command(
        aliases = {"win"},
        flags = ":t:p",
        desc = "Win the match"
    )
    @Beta
    public static void win(CommandContext args, CommandSender sender) throws CommandException {
        // checks to only win the game if the game is running
        try {
            checkArgument(!MapNodes.getCurrentGame().getStage().isPreGame());
            checkArgument(!MapNodes.getCurrentGame().getStage().isEndGame());
        }
        catch (IllegalArgumentException e) {
            throw new CommandException(e.getMessage());
        }

        // win the game with the specific team
        try {
            // Win as the specific player
            if (args.hasFlag('p')) {
                GamePlayer player = MapNodes.getCurrentGame().getPlaying()
                    .filter(p -> p.getPlayer().getName().equalsIgnoreCase(args.getFlag('p')))
                    .collect(Collectors.toList()).get(0);
                new GamePlayerWinEvent(MapNodes.getCurrentGame(), player).call();
            }
            // Win as the specific team
            else if (args.hasFlag('t')) {
                GameTeam team = MapNodes.getCurrentGame().getTeams().get(args.getFlag('t'));
                new GameTeamWinEvent(MapNodes.getCurrentGame(), team).call();
            }
            // Just win the game
            else {
                new GameWinEvent().call();
            }
        }
        catch (Exception e) {
            throw new CommandException(e);
        }
    }
}
