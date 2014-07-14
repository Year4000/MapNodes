package net.year4000.mapnodes.game;

import com.ewized.utilities.bukkit.util.MessageUtil;
import net.year4000.mapnodes.configs.Messages;

import java.util.ArrayList;

public class GameHelper {

    /** Start message for when players join the game.p */
    public static void startMessage(NodePlayer player) {
        if (player.isSpecatator()) return;

        GameManager gm = WorldManager.get().getCurrentGame();
        ArrayList<String> messages = new ArrayList<>();
        String authors = "";
        for (String author : gm.getMap().getAuthors())
            authors += authors.equals("") ? "&a" + author : "&7, &a" + author;

        messages.add("");
        messages.add(Messages.get(player.getPlayer().getLocale(), "game-start-top"));
        messages.add(String.format(
            Messages.get(player.getPlayer().getLocale(), "game-start-map"),
            gm.getMap().getName(),
            gm.getMap().getVersion()
        ));
        messages.add(String.format(
            Messages.get(player.getPlayer().getLocale(), "game-start-authors"),
            authors
        ));
        messages.add(String.format(
            Messages.get(player.getPlayer().getLocale(), "game-start-description"),
            gm.getMap().getDescription()
        ));
        messages.add(Messages.get(player.getPlayer().getLocale(), "game-start-bottom"));
        messages.add("");

        for (String message : messages)
            player.getPlayer().sendMessage(MessageUtil.replaceColors(message));
    }

    /** End message for when players end the game. */
    public static void endMessage(NodePlayer player) {
        if (player.isSpecatator()) return;

        ArrayList<String> messages = new ArrayList<>();

        messages.add("");
        messages.add(Messages.get(player.getPlayer().getLocale(), "game-end-top"));
        messages.add(String.format(
            Messages.get(player.getPlayer().getLocale(), "game-end-team"),
            player.getTeam().getDisplayName()
        ));
        messages.add(String.format(
            Messages.get(player.getPlayer().getLocale(), "game-end-player-score"),
            player.getScore()
        ));
        messages.add(String.format(
            Messages.get(player.getPlayer().getLocale(), "game-end-team-score"),
            player.getTeam().getScore()
        ));
        messages.add(Messages.get(player.getPlayer().getLocale(), "game-end-bottom"));
        messages.add("");

        for (String message : messages)
            player.getPlayer().sendMessage(MessageUtil.replaceColors(message));
    }
}
