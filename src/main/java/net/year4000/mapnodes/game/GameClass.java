package net.year4000.mapnodes.game;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import net.year4000.mapnodes.configs.Messages;
import net.year4000.mapnodes.configs.map.Classes;
import net.year4000.mapnodes.utils.ClassException;
import net.year4000.mapnodes.world.WorldManager;
import net.year4000.utilities.bukkit.BukkitUtil;
import net.year4000.utilities.bukkit.ItemUtil;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static com.google.common.base.Preconditions.checkNotNull;


@Data
@Setter(AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class GameClass {
    /** The name of the class. */
    private String name = null;
    /** The icon item for the class. */
    private ItemStack icon = null;
    /** The description of the class. */
    private String description = null;
    /** The kit name to use with this class. */
    private GameKit kit = null;
    /** The permission to use this class. */
    private String permission = "";
    /** The permission message to display. */
    private String permMessage = "This kit requires a vip account.";
    /** The inventory to select the classes from. */
    private static Inventory classesGUI = null;

    /** Set up this class to be used in game. */
    public GameClass(Classes classes, GameManager gm) throws NullPointerException {
        // The name of the class
        setName(checkNotNull(classes.getName(), Messages.get("error-json-class-name")));

        // The description of the class
        setDescription(checkNotNull(classes.getDescription(), Messages.get("error-json-class-description")));

        // Set up the permission
        if (!classes.getPermission().equals("")) {
            setPermission(classes.getPermission());
            setPermMessage(classes.getPermMessage().equals("") ? getPermMessage() : classes.getPermMessage());
        }

        // Create the icon for this class
        String message = "\"";
        int counter = 0;
        for (String word : getDescription().split(" ")) {
            counter ++;
            boolean last = getDescription().split(" ").length == counter;

            message += word + " ";

            if (counter > 6 || last) {
                message += "\", \"";
                counter = 0;
            }
        }
        message += "\"";

        setIcon(checkNotNull(ItemUtil.makeItem(
            checkNotNull(classes.getIcon().toUpperCase(), Messages.get("error-json-class-icon")),
            MessageUtil.replaceColors(String.format(
                "{\"display\": {\"name\": \"%s\", \"lore\": [\"\",%s%s]}}",
                getName(),
                message,
                getPermission().equals("") ? "" : ",\"\",\"&6" + getPermMessage() + "\""
            ))
        ), Messages.get("error-json-class-icon")));

        // Set up the kit
        setKit(checkNotNull(gm.getKits().get(classes.getKit().toUpperCase()), Messages.get("error-json-class-kit")));
    }

    /** Give this class to the player. */
    public void give(GamePlayer player) throws ClassException {
        // Prevent players from entering when the game ended!
        if (GameStage.isEndGame())
            throw new ClassException(Messages.get(player.getPlayer().getLocale(), "class-join-error"));

        // Check if the player has the permission
        if (!GameManager.isMapMaker(player)) {
            if (!getPermission().equals("")) {
                if (!player.getPlayer().hasPermission(getPermission()))
                    throw new ClassException(getPermMessage());
            }
        }

        player.setTeamClass(this);

        player.getPlayer().sendMessage(MessageUtil.replaceColors(String.format(
            Messages.get(player.getPlayer().getLocale(), "class-join"),
            getName()
        )));
    }

    /** Get the Inventory to pick teams. */
    public static Inventory getClassesGUI() {
        GameManager gm = WorldManager.get().getCurrentGame();
        classesGUI = Bukkit.createInventory(
            null,
            BukkitUtil.invBase(gm.getTeamClasses().size()),
            Messages.get("class-gui-title")
        );

        // Load all classes to inventory
        for (GameClass classes : gm.getTeamClasses().values()) {
            classesGUI.addItem(classes.getIcon());
        }

        return classesGUI;
    }

}
