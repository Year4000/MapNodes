/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.games.gui;

import com.google.common.collect.Iterators;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.utilities.bukkit.BukkitUtil;
import net.year4000.utilities.bukkit.ItemUtil;
import net.year4000.utilities.bukkit.gui.ActionMeta;
import net.year4000.utilities.bukkit.gui.IconView;
import net.year4000.utilities.bukkit.gui.InventoryGUI;
import net.year4000.utilities.bukkit.items.NBTBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.Locale;

import static com.google.common.base.Preconditions.checkNotNull;

public class BiomeManager extends MapNodesLocaleGUI implements IconView {
    private static final Iterator<Material> BIOMES = Iterators.cycle(BiomeView.MAPPER.values());
    private final int size = BukkitUtil.invBase(BiomeView.MAPPER.size());
    private PlotManager manager;

    public BiomeManager(PlotManager manager) {
        this.manager = checkNotNull(manager, "manager");
        populateMenu(locale -> {
            generate(locale);
            return Msg.locale(manager.getGamePlayer(), "builders.plot.biome");
        }, size);
        MapNodes.getCurrentGame().addTask(SchedulerUtil.repeatSync(this, 10L));
    }

    @Override
    public IconView[][] generate(Locale locale) {
        IconView[][] view = new IconView[size][InventoryGUI.COLS];
        Iterator<BiomeView> views = BiomeView.biomeFactory(manager).iterator();
        boolean broken = false;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < InventoryGUI.COLS; j++) {
                if (views.hasNext()) {
                    view[i][j] = views.next();
                }
                else {
                    broken = true;
                    break;
                }
            }

            if (broken) break;
        }

        return view;
    }

    @Override
    public ItemStack make() {
        Material material = BiomeView.MAPPER.getOrDefault(manager.getPlot().getBiome(), BIOMES.next());
        ItemStack item = ItemUtil.makeItem(material);
        NBTBuilder.of(item).setDisplayName(Msg.locale(manager.getGamePlayer(), "builders.plot.biome"));
        return item;
    }

    @Override
    public boolean action(Player player, ActionMeta meta, InventoryGUI gui) {
        openInventory(player);
        return true;
    }
}
