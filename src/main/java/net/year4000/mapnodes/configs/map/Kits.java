package net.year4000.mapnodes.configs.map;

import com.ewized.utilities.bukkit.util.ItemUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@SuppressWarnings("all")
/** Manage the items and effects that are given to the player. */
public class Kits {
    /** The parent kit. */
    private String[] inherent = new String[] {};
    /** The items to put in the player's inventory. */
    private Items[] items = null;
    /** The potion effects to add to the player. */
    private Effects[] effects = null;
    /** The armor for the player. */
    private Armor armor = null;
    /** The kits game mode. */
    private String gamemode = "SURVIVAL";
    /** The kits's health level */
    private int health = 20;
    /** The kits's food level */
    private int food = 20;
    /** The kits permissions */
    private String[] permissions = new String[] {};
    /** Can this kit fly */
    private boolean fly = false;

    @Data
    @NoArgsConstructor
    /** Armor */
    public class Armor {
        /** The slot number to appear in the inventory. */
        private Items helmet;
        private Items chestplate;
        private Items leggings;
        private Items boots;
    }

    @Data
    @NoArgsConstructor
    /** Items */
    public class Items {
        /** The slot number to appear in the inventory. */
        private Integer slot = null;
        /** The amount of items to give. */
        private Integer amount = null;
        /** The damage of the item. */
        private Short damage = null;
        /** The name of the item. */
        private String item = null;
        /** NBT data of the item. */
        private ItemUtil.Nbt nbt;
    }

    @Data
    @NoArgsConstructor
    /** Potion effects. */
    public class Effects {
        /** The id of the effect. */
        private String name;
        /** The time the effect will last in minecraft ticks. */
        private int duration = 60;
        /** The level the effect will have. */
        private int amplifier = 0;
        /** Decrease and translucent particle effects. */
        private boolean ambient = false;
        /** Wheather or not to show particles. */
        private boolean showParticles = true;
    }
}
