/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.game.system;

import net.year4000.mapnodes.api.utils.Spectator;
import net.year4000.mapnodes.game.NodeKit;
import net.year4000.mapnodes.utils.typewrappers.PlayerArmorList;
import net.year4000.mapnodes.utils.typewrappers.PlayerInventoryList;
import net.year4000.utilities.bukkit.ItemUtil;
import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpectatorKit extends NodeKit implements Spectator {
    public SpectatorKit() {
        fly = true;

        gamemode = GameMode.ADVENTURE;

        effects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        effects.add(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
        effects.add(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));

        items = new PlayerInventoryList<ItemStack>() {{
            for (int i = 0; i < 36; i++) {
                add(ItemUtil.makeItem("air"));
            }
        }};

        armor = new PlayerArmorList<>();
        armor.add(ItemUtil.makeItem("air"));
        armor.add(ItemUtil.makeItem("air"));
        armor.add(ItemUtil.makeItem("air"));
        armor.add(ItemUtil.makeItem("air"));
    }
}
