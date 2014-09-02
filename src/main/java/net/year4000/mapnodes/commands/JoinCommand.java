package net.year4000.mapnodes.commands;

import net.year4000.mapnodes.MapNodes;
import net.year4000.mapnodes.configs.Messages;
import net.year4000.mapnodes.game.GameManager;
import net.year4000.mapnodes.game.GamePlayer;
import net.year4000.mapnodes.game.GameStage;
import net.year4000.mapnodes.game.GameTeam;
import net.year4000.mapnodes.utils.TeamException;
import net.year4000.mapnodes.world.WorldManager;
import net.year4000.utilities.bukkit.commands.Command;
import net.year4000.utilities.bukkit.commands.CommandContext;
import net.year4000.utilities.bukkit.commands.CommandException;
import net.year4000.utilities.bukkit.commands.CommandsManagerRegistration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class JoinCommand {
    public JoinCommand() {
        new CommandsManagerRegistration(
            MapNodes.getInst(),
            MapNodes.getInst().getCommands()
        ).register(JoinCommand.class);
    }

    @Command(
        aliases = {"join", "team", "teams"},
        usage = "(team)",
        desc = "Join a team or open the team gui.",
        min = 0,
        max = 1
    )
    public static void join(CommandContext args, CommandSender sender) throws CommandException {
        if (!(sender instanceof Player))
            throw new CommandException(Messages.get("command-console"));

        Player player = Bukkit.getPlayer(sender.getName()).getPlayer();

        if (GameStage.isEndGame())
            throw new CommandException(Messages.get(player.getLocale(), "team-join-error"));

        GameManager gm = WorldManager.get().getCurrentGame();

        if (!gm.getPlayer((Player)sender).isSpecatator())
            throw new CommandException(Messages.get(player.getLocale(), "command-team-spectator"));

        if (args.argsLength() == 0) {
            ((Player) sender).openInventory(GameTeam.getTeamsGUI());
        }
        else {
            try {
                gm.getTeam(args.getString(0)).join(gm.getPlayer((Player)sender));
            } catch (TeamException e) {
                throw new CommandException(e.getRawMessage());
            } catch (NullPointerException e) {
                throw new CommandException(Messages.get(player.getLocale(), "command-team-unknown"));
            }
        }
    }

    @Command(
        aliases = {"spectator", "spec"},
        desc = "Join the spectator team and watch the game.",
        max = 0
    )
    public static void spectator(CommandContext args, CommandSender sender) throws CommandException {
        if (!(sender instanceof Player))
            throw new CommandException(Messages.get("command-console"));

        Player player = Bukkit.getPlayer(sender.getName()).getPlayer();

        GameManager gm = WorldManager.get().getCurrentGame();
        GamePlayer gPlayer = gm.getPlayer((Player)sender);

        if (gPlayer.isSpecatator() || !gPlayer.isHasPlayed())
            throw new CommandException(Messages.get(player.getLocale(), "command-team-player"));

        gPlayer.leave();
        GamePlayer.join((Player)sender);
        //gm.getPlayer((Player)sender).getPlayer().setHealth(0);
    }
}
