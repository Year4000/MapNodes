package net.year4000.mapnodes.game.components;

import com.google.gson.annotations.Since;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.GameMap;
import net.year4000.mapnodes.exceptions.InvalidJsonException;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Common;
import net.year4000.mapnodes.utils.Validator;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

@Data
@NoArgsConstructor
/** Details about the current map. */
public final class NodeMap implements GameMap, Validator {
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

    /** Get main author */
    public String getMainAuthor() {
        return authors.get(0);
    }

    /** Get other authors */
    public List<String> getOtherAuthors() {
        List<String> others = new ArrayList<>(authors);
        others.remove(0);
        return others;
    }

    /** Get the description in your own locale */
    public String getDescription(String locale) {
        return MapNodes.getCurrentGame().locale(locale, description);
    }

    /** Get multi line description */
    public List<String> getMultiLineDescription(String locale) {
        List<String> lines = new ArrayList<>();
        String[] spited = MapNodes.getCurrentGame().locale(locale, description).split(" ");

        String line = "";
        int counter = 0;
        for (String word : spited) {
            counter ++;
            boolean last = spited.length == counter;

            line += word + " ";

            if (counter > 6 || last) {
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
}