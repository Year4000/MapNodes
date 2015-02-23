package net.year4000.mapnodes.game;

import com.google.gson.annotations.Since;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.year4000.mapnodes.api.exceptions.InvalidJsonException;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.api.game.GameMap;
import net.year4000.mapnodes.messages.Message;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Common;
import net.year4000.utilities.ChatColor;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

@Data
@NoArgsConstructor
/** Details about the current map. */
public final class NodeMap implements GameMap {
    /** The name of the current map. */
    @Since(1.0)
    private String name = null;

    /** The version of the current map. */
    @Since(1.0)
    private String version = null;

    /** The object/goal of the current map. */
    @Since(1.0)
    private String description = null;

    /** Any one that has helped with current the map. */
    @Since(1.0)
    private List<String> authors = new ArrayList<>();
    private transient GameManager game;

    @Override
    public void validate() throws InvalidJsonException {
        checkArgument(name != null, Msg.util("settings.map.name"));

        checkArgument(version != null, Msg.util("settings.map.version"));

        checkArgument(description != null, Msg.util("settings.map.description"));

        checkArgument(authors.size() > 0, Msg.util("settings.map.authors"));
    }

    /*//--------------------------------------------//
         Upper Json Settings / Bellow Instance Code
    *///--------------------------------------------//

    /** Assign the game to this region */
    public void assignNodeGame(GameManager game) {
        this.game = game;
    }

    /** Get main author */
    public String getMainAuthor() {
        return authors.get(0);
    }

    /** Map title includes map name and map version */
    public String title() {
        return ChatColor.GREEN + name + " " + Common.formatSeparators(version, ChatColor.GRAY, ChatColor.DARK_GRAY);
    }

    /** Get other authors */
    public List<String> getOtherAuthors() {
        List<String> others = new ArrayList<>(authors);
        others.remove(0);
        return others;
    }

    /** Get the description in your own locale */
    public String getDescription(String locale) {
        return game.locale(locale, description);
    }

    /** Get multi line description */
    public List<String> getMultiLineDescription(String locale) {
        return getMultiLineDescription(locale, 6);
    }

    public List<String> getMultiLineDescription(String locale, int size) {
        List<String> lines = new ArrayList<>();
        String[] spited = game.locale(locale, description).split(" ");

        String line = "";
        int counter = 0;

        for (String word : spited) {
            counter++;
            boolean last = spited.length == counter;

            line += word + " ";

            if (counter > size || last) {
                lines.add(line);
                line = "";
                counter = 0;
            }
        }

        lines.add(line);

        return lines;
    }

    /** Does this map have more than one author */
    public boolean hasOtherAuthors() {
        return getAuthors().size() != 1;
    }

    /** Get short description messages */
    public String getShortDescription(int size) {
        return Common.shortMessage(size, description);
    }

    /** Get the book page for this map */
    public List<String> getBookPage(Player player) {
        List<String> lines = new ArrayList<>();

        lines.add(MessageUtil.message("\n\n&6%s", name));
        lines.add(MessageUtil.message("&0%s&7: &5%s", Msg.locale(player, "map.by"), getMainAuthor()));
        lines.add(MessageUtil.message("\n\n&0%s", getShortDescription(45)));

        return lines;
    }

    /** Fancy authors display */
    public String author(String locale) {
        if (hasOtherAuthors()) {
            int size = getOtherAuthors().size();

            if (size == 1) {
                return Msg.locale(locale, "map.author_duo", getMainAuthor(), getOtherAuthors().get(0));
            }
            else {
                return Msg.locale(locale, "map.authors", getMainAuthor(), String.valueOf(size));
            }
        }

        return Msg.locale(locale, "map.author", getMainAuthor());
    }

    /** Fancy authors display */
    public String author(CommandSender sender) {
        if (sender instanceof Player) {
            return author(((Player) sender).getLocale());
        }
        else {
            return author(Message.DEFAULT_LOCALE);
        }
    }
}