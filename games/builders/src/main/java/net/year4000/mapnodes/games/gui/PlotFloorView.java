/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.games.gui;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.games.PlayerPlot;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.utilities.bukkit.gui.ActionMeta;
import net.year4000.utilities.bukkit.gui.IconView;
import net.year4000.utilities.bukkit.gui.InventoryGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static com.google.common.base.Preconditions.checkNotNull;

public class PlotFloorView implements IconView {
    private static final BiMap<Material, Material> MAPPER = ImmutableBiMap.<Material, Material>builder()
        .put(Material.LAVA_BUCKET, Material.STATIONARY_LAVA)
        .put(Material.WATER_BUCKET, Material.STATIONARY_WATER)
        .put(Material.AIR, Material.BARRIER)
        .put(Material.FIREBALL, Material.FIRE)
        .build();
    private PlotManager manager;
    private PlayerPlot plot;

    public PlotFloorView(PlotManager manager) {
        this.manager = checkNotNull(manager);
        this.plot = manager.getPlot();
    }

    @Override
    public ItemStack make() {
        Material iconMaterial = MAPPER.inverse().getOrDefault(plot.getFloor(), plot.getFloor());
        ItemStack icon = new ItemStack(iconMaterial, 1, plot.getFloorData());
        ItemMeta meta = icon.getItemMeta();
        meta.setDisplayName(Msg.locale(manager.getGamePlayer(), "builders.plot.floor.icon"));
        icon.setItemMeta(meta);
        return icon;
    }

    @Override
    public boolean action(Player player, ActionMeta meta, InventoryGUI gui) {
        ItemStack current = meta.getCursor();
        Material material = current.getType();
        GamePlayer gamePlayer = MapNodes.getCurrentGame().getPlayer(player);

        if (material == Material.AIR) {
            return true;
        }
        else if ((material.isBlock() || MAPPER.containsKey(material))) {
            material = MAPPER.getOrDefault(material, material);
            String block = material.name().toLowerCase().replace("_", " ");
            gamePlayer.sendMessage(Msg.locale(gamePlayer, "builders.plot.floor.set", block));
            plot.setFloor(material, current.getDurability());
        }
        else {
            gamePlayer.sendMessage(Msg.locale(gamePlayer, "builders.plot.floor.block"));
        }

        //meta.setCursor(null);
        return true;
    }
}
