package net.year4000.mapnodes.game;

import com.ewized.utilities.bukkit.util.MessageUtil;
import net.year4000.mapnodes.configs.Messages;
import net.year4000.mapnodes.world.WorldManager;

import java.util.ArrayList;

public class GameHelper {

    /** Start message for when players join the game.p */
    public static void startMessage(GamePlayer player) {
        if (player.isSpecatator()) return;

        GameManager gm = WorldManager.get().getCurrentGame();
        ArrayList<String> messages = new ArrayList<>();
        String authors = "";
        for (String author : gm.getMap().getAuthors())
            authors += authors.equals("") ? "&a" + author : "&7, &a" + author;

        messages.add("");
        messages.add(Messages.get("game.start.top"));
        messages.add(String.format(
            Messages.get("game.start.map"),
            gm.getMap().getName(),
            gm.getMap().getVersion()
        ));
        messages.add(String.format(
            Messages.get("game.start.authors"),
            authors
        ));
        messages.add(String.format(
            Messages.get("game.start.description"),
            gm.getMap().getDescription()
        ));
        messages.add(Messages.get("game.start.bottom"));
        messages.add("");

        for (String message : messages)
            player.getPlayer().sendMessage(MessageUtil.replaceColors(message));
    }

    /** End message for when players end the game. */
    public static void endMessage(GamePlayer player) {
        if (player.isSpecatator()) return;

        ArrayList<String> messages = new ArrayList<>();

        messages.add("");
        messages.add(Messages.get("game.end.top"));
        messages.add(String.format(
            Messages.get("game.end.team"),
            player.getTeam().getDisplayName()
        ));
        messages.add(String.format(
            Messages.get("game.end.player.score"),
            player.getScore()
        ));
        messages.add(String.format(
            Messages.get("game.end.team.score"),
            player.getTeam().getScore()
        ));
        messages.add(Messages.get("game.end.bottom"));
        messages.add("");

        for (String message : messages)
            player.getPlayer().sendMessage(MessageUtil.replaceColors(message));
    }
}
