package net.year4000.mapnodes.commands;

import com.ewized.utilities.bukkit.util.MessageUtil;
import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import net.year4000.mapnodes.MapNodes;
import net.year4000.mapnodes.configs.Messages;
import net.year4000.mapnodes.game.GameManager;
import net.year4000.mapnodes.game.GameTeam;
import net.year4000.mapnodes.world.WorldManager;
import org.bukkit.command.CommandSender;
import org.joda.time.DateTime;

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
        GameManager gm = WorldManager.get().getCurrentGame();
        ArrayList<String> messages = new ArrayList<>();
        String authors = "";
        for (String author : gm.getMap().getAuthors())
            authors += authors.equals("") ? "&a" + author : "&7, &a" + author;

        messages.add("");
        messages.add(Messages.get("command.info.top"));
        messages.add(String.format(
            Messages.get("command.info.map"),
            gm.getMap().getName(),
            gm.getMap().getVersion()
        ));
        messages.add(String.format(
            Messages.get("command.info.authors"),
            authors
        ));
        messages.add(String.format(
            Messages.get("command.info.description"),
            gm.getMap().getDescription()
        ));
        messages.add("");
        /* TIME MESSAGES HERE
        messages.add(String.format(
            Messages.get("command.info.time"),
            new DateTime(gm.getStartTime()).toString()
        ));
        messages.add("");
        */
        messages.add(Messages.get("command.info.team.top"));
        for (GameTeam team : gm.getTeams().values()) {
            if (team.getName().equals("SPECTATOR")) continue;
            messages.add(String.format(
                Messages.get("command.info.team"),
                team.getDisplayName(),
                team.getColorSize(),
                team.getScore()
            ));
        }
        messages.add(Messages.get("command.info.bottom"));
        messages.add("");

        for (String message : messages)
            sender.sendMessage(MessageUtil.replaceColors(message));
    }
}
