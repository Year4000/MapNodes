package net.year4000.mapnodes.game;

import com.google.gson.annotations.Since;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.year4000.mapnodes.api.game.GameClass;
import net.year4000.mapnodes.exceptions.InvalidJsonException;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.AssignNodeGame;
import net.year4000.mapnodes.utils.NMSHacks;
import net.year4000.mapnodes.utils.Validator;
import net.year4000.utilities.MessageUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Data
@NoArgsConstructor
/** Classes to pick from on top of your team. */
public final class NodeClass implements GameClass, Validator, AssignNodeGame {
    /** The name of the class. */
    @Since(1.0)
    private String name = null;

    /** The icon item for the class. */
    @Since(1.0)
    private Material icon = null;

    /** The description of the class. */
    @Since(1.0)
    private String description = null;

    /** The kit name to use with this class. */
    @Since(1.0)
    private String kit = null;

    @Override
    public void validate() throws InvalidJsonException {
        if (name == null) {
            throw new InvalidJsonException(Msg.util("settings.class.name"));
        }

        if (icon == null) {
            throw new InvalidJsonException(Msg.util("settings.class.icon"));
        }

        if (description == null) {
            throw new InvalidJsonException(Msg.util("settings.class.description"));
        }

        if (kit == null) {
            throw new InvalidJsonException(Msg.util("settings.class.kit"));
        }
    }

    /*//--------------------------------------------//
         Upper Json Settings / Bellow Instance Code
    *///--------------------------------------------//

    private transient NodeGame game;
    @Setter(AccessLevel.NONE)
    private transient String id;

    /** Assign the game to this region */
    public void assignNodeGame(NodeGame game) {
        this.game = game;
    }

    /** Get the id of this class and cache it */
    public String getId() {
        if (id == null) {
            NodeClass thisObject = this;

            game.getClasses().forEach((string, object) -> {
                if (object.equals(thisObject)) {
                    id = string;
                }
            });
        }

        return id;
    }

    public NodeKit getKit() {
        return game.getKits().get(kit);
    }

    public List<String> getMultiLineDescription(String locale, int size) {
        List<String> lines = new ArrayList<>();
        String[] spited = game.locale(locale, description).split(" ");

        String line = "";
        int counter = 0;

        for (String word : spited) {
            counter ++;
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

    /** Create the item icon for this class */
    public ItemStack createClassIcon(Locale locale) {
        ItemStack kitIcon = new ItemStack(icon, 1);
        ItemMeta kitMeta = kitIcon.getItemMeta();

        kitMeta.setDisplayName(MessageUtil.replaceColors("&a&l" + name));
        List<String> lore = getMultiLineDescription(locale.toString(), 6);
        lore.add("");
        lore.add(Msg.locale(locale.toString(), "class.menu.join"));
        kitMeta.setLore(lore);
        kitIcon.setItemMeta(kitMeta);

        NMSHacks.setNBTTag(kitIcon, "MapNodes_Class", getId());

        return kitIcon;
    }
}
