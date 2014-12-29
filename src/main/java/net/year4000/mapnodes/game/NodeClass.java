package net.year4000.mapnodes.game;

import com.google.gson.annotations.Since;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.year4000.mapnodes.api.exceptions.InvalidJsonException;
import net.year4000.mapnodes.api.game.GameClass;
import net.year4000.mapnodes.api.game.GameComponent;
import net.year4000.mapnodes.api.game.GameKit;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.NMSHacks;
import net.year4000.utilities.MessageUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
/** Classes to pick from on top of your team. */
public class NodeClass implements GameClass, GameComponent {
    /** The name of the class. */
    @Since(1.0)
    protected String name = null;
    /** The icon item for the class. */
    @Since(1.0)
    protected Material icon = null;
    /** The description of the class. */
    @Since(1.0)
    protected String description = null;
    /** The kit name to use with this class. */
    @Since(1.0)
    protected String kit = null;


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

    @Getter(lazy = true)
    private final transient String id = id();
    private transient GameManager game;

    public NodeClass(NodeGame game, String name, Material icon, String description, String kit) {
        assignNodeGame(game);
        this.name = name;
        this.icon = icon;
        this.description = description;
        this.kit = kit;
    }

    /** Assign the game to this region */
    public void assignNodeGame(GameManager game) {
        this.game = game;
    }

    /** Get the id of this class and cache it */
    private String id() {
        NodeClass thisObject = this;

        for (Map.Entry<String, GameClass> entry : game.getClasses().entrySet()) {
            if (thisObject.equals(entry.getValue())) {
                return entry.getKey();
            }
        }

        throw new RuntimeException("Can not find the id of " + this.toString());
    }

    public GameKit getKit() {
        return game.getKits().get(kit);
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
