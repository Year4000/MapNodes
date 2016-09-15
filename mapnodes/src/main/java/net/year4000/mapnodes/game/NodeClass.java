/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.game;

import com.google.gson.annotations.Since;
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
    /** The kit name to use with this class. */
    @Since(1.0)
    protected String permission = null;

    public NodeClass() {
    }

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

    private final transient String id = id();
    private transient GameManager game;

    public NodeClass(NodeGame game, String name, Material icon, String description, String kit) {
        this(game, name, icon, description, kit, null);
    }

    public NodeClass(NodeGame game, String name, Material icon, String description, String kit, String permission) {
        assignNodeGame(game);
        this.name = name;
        this.icon = icon;
        this.description = description;
        this.kit = kit;
        this.permission = permission;
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

    public String getName() {
        return this.name;
    }

    public Material getIcon() {
        return this.icon;
    }

    public String getDescription() {
        return this.description;
    }

    public String getPermission() {
        return this.permission;
    }

    public GameManager getGame() {
        return this.game;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof NodeClass)) return false;
        final NodeClass other = (NodeClass) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final Object this$icon = this.getIcon();
        final Object other$icon = other.getIcon();
        if (this$icon == null ? other$icon != null : !this$icon.equals(other$icon)) return false;
        final Object this$description = this.getDescription();
        final Object other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description))
            return false;
        final Object this$kit = this.getKit();
        final Object other$kit = other.getKit();
        if (this$kit == null ? other$kit != null : !this$kit.equals(other$kit)) return false;
        final Object this$permission = this.getPermission();
        final Object other$permission = other.getPermission();
        if (this$permission == null ? other$permission != null : !this$permission.equals(other$permission))
            return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $icon = this.getIcon();
        result = result * PRIME + ($icon == null ? 43 : $icon.hashCode());
        final Object $description = this.getDescription();
        result = result * PRIME + ($description == null ? 43 : $description.hashCode());
        final Object $kit = this.getKit();
        result = result * PRIME + ($kit == null ? 43 : $kit.hashCode());
        final Object $permission = this.getPermission();
        result = result * PRIME + ($permission == null ? 43 : $permission.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof NodeClass;
    }

    public String getId() {
        return this.id;
    }
}
