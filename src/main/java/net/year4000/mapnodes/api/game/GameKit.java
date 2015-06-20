/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.api.game;

import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public interface GameKit extends GameComponent {
    public List<String> getParents();

    public List<ItemStack> getItems();

    public List<PotionEffect> getEffects();

    public List<ItemStack> getArmor();

    public GameMode getGamemode();

    public int getHealth();

    public int getFood();

    public List<String> getPermissions();

    public boolean isFly();

    public void giveKit(GamePlayer player);

    public List<ItemStack> getNonAirItems();
}
