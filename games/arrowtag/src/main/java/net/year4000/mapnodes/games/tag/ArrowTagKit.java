/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.games.tag;

import net.year4000.mapnodes.api.game.GameKit;
import net.year4000.mapnodes.game.NodeKit;
import net.year4000.mapnodes.utils.typewrappers.PlayerInventoryList;
import net.year4000.utilities.bukkit.ItemUtil;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ArrowTagKit extends NodeKit implements GameKit {
    public static final String NAME = "arrow_tag_kit";
    public static final String ITEM_NAME = MessageUtil.replaceColors("&aArrow &6Tag&7&o");
    public static final String ITEM_NAME_ALT = MessageUtil.replaceColors("&6Arrow &aTag&7&o");
    public static final int ARROW_SLOT = 8;
    public static final int BOW_SLOT = 0;
    public static final int AXE_SLOT = 1;

    public ArrowTagKit() {
        items = new PlayerInventoryList<ItemStack>() {{
            for (int i = 0; i < 36; i++) {
                add(ItemUtil.makeItem("air"));
            }
        }};

        items.get(ARROW_SLOT).setType(Material.ARROW);
        ItemMeta metaArrow = items.get(ARROW_SLOT).getItemMeta();
        metaArrow.setDisplayName(ITEM_NAME_ALT);
        items.get(ARROW_SLOT).setItemMeta(metaArrow);

        items.get(BOW_SLOT).setType(Material.BOW);
        ItemMeta metaBow = items.get(BOW_SLOT).getItemMeta();
        metaBow.setDisplayName(ITEM_NAME);
        items.get(BOW_SLOT).setItemMeta(metaBow);
        items.get(BOW_SLOT).setDurability((short) -3000);


        items.get(AXE_SLOT).setType(Material.STONE_AXE);
        ItemMeta metaAxe = items.get(AXE_SLOT).getItemMeta();
        metaAxe.setDisplayName(ITEM_NAME_ALT);
        items.get(AXE_SLOT).setItemMeta(metaAxe);
        items.get(AXE_SLOT).setDurability((short) -3000);

        effects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
    }
}
