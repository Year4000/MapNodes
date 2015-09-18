/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.games.builders.gui;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Common;
import net.year4000.utilities.bukkit.gui.ActionMeta;
import net.year4000.utilities.bukkit.gui.IconView;
import net.year4000.utilities.bukkit.gui.InventoryGUI;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

public class BiomeView implements IconView {
    static final BiMap<Biome, Material> MAPPER = ImmutableBiMap.<Biome, Material>builder()
        .put(Biome.PLAINS, Material.GRASS)
        .put(Biome.BEACH, Material.SAND)
        .put(Biome.HELL, Material.NETHERRACK)
        .put(Biome.SKY, Material.ENDER_STONE)
        .put(Biome.JUNGLE, Material.JUNGLE_FENCE)
        .build();
    private PlotManager manager;
    private Biome biome;

    private BiomeView(PlotManager manager, Biome biome) {
        this.manager = checkNotNull(manager, "manager");
        this.biome = checkNotNull(biome, "biome");
    }

    /** Get the list of al Biome Views */
    public static List<BiomeView> biomeFactory(PlotManager manager) {
        return MAPPER.keySet().stream().map(biome -> new BiomeView(manager, biome)).collect(Collectors.toList());
    }

    @Override
    public ItemStack make() {
        Material iconMaterial = MAPPER.get(biome);
        ItemStack icon = new ItemStack(iconMaterial, 1);
        ItemMeta meta = icon.getItemMeta();
        meta.setDisplayName(Msg.locale(manager.getGamePlayer(), "builders.plot.biome.icon", Common.prettyEnum(iconMaterial)));
        icon.setItemMeta(meta);
        return icon;
    }

    @Override
    public boolean action(Player player, ActionMeta meta, InventoryGUI gui) {
        GamePlayer gamePlayer = MapNodes.getCurrentGame().getPlayer(player);
        String out = Common.prettyEnum(MAPPER.get(biome));
        player.sendMessage(Msg.locale(gamePlayer, "builders.plot.biome.set", out));
        manager.getPlot().setBiome(biome);
        // todo send client updated chunks
        return true;
    }
}
