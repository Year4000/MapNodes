package net.year4000.mapnodes.commands;

import net.year4000.mapnodes.MapNodes;
import net.year4000.mapnodes.configs.Messages;
import net.year4000.mapnodes.game.GameManager;
import net.year4000.mapnodes.game.GameTeam;
import net.year4000.mapnodes.world.WorldManager;
import net.year4000.utilities.bukkit.MessageUtil;
import net.year4000.utilities.bukkit.commands.Command;
import net.year4000.utilities.bukkit.commands.CommandContext;
import net.year4000.utilities.bukkit.commands.CommandException;
import net.year4000.utilities.bukkit.commands.CommandsManagerRegistration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

@SuppressWarnings("unused")
public final class GameCommand {
    public GameCommand() {
        new CommandsManagerRegistration(
            MapNodes.getInst(),
            MapNodes.getInst().getCommands()
        ).register(GameCommand.class);
    }

    @Command(
        aliases = {"game", "status", "stats"},
        desc = "View info of the current game",
        max = 0
    )
    public static void game(CommandContext args, CommandSender sender) throws CommandException {
        OfflinePlayer player = Bukkit.getPlayer(sender.getName());
        String locale = player == null ? "messages" : player.getPlayer().getLocale();

        GameManager gm = WorldManager.get().getCurrentGame();
        ArrayList<String> messages = new ArrayList<>();
        String authors = "";
        for (String author : gm.getMap().getAuthors())
            authors += authors.equals("") ? "&a" + author : "&7, &a" + author;

        messages.add("");
        messages.add(Messages.get(locale, "command-info-top"));
        messages.add(String.format(
            Messages.get(locale, "command-info-map"),
            gm.getMap().getName(),
            gm.getMap().getVersion()
        ));
        messages.add(String.format(
            Messages.get(locale, "command-info-authors"),
            authors
        ));
        messages.add(String.format(
            Messages.get(locale, "command-info-description"),
            gm.getMap().getDescription()
        ));
        messages.add("");
        /* TIME MESSAGES HERE
        messages.add(String.format(
            Messages.get(locale, "command.info.time"),
            new DateTime(gm.getStartTime()).toString()
        ));
        messages.add("");
        */
        messages.add(Messages.get(locale, "command-info-team-top"));
        for (GameTeam team : gm.getTeams().values()) {
            if (team.getName().equals("SPECTATOR")) continue;
            messages.add(String.format(
                Messages.get(locale, "command-info-team"),
                team.getDisplayName(),
                team.getColorSize(),
                team.getScore()
            ));
        }
        messages.add(Messages.get(locale, "command-info-bottom"));
        messages.add("");

        messages.forEach( message -> sender.sendMessage(MessageUtil.message(message)));
    }
}
