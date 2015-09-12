package net.year4000.mapnodes.games.gui;

import com.google.common.collect.ImmutableMap;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.games.PlayerPlot;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Common;
import net.year4000.utilities.bukkit.ItemUtil;
import net.year4000.utilities.bukkit.gui.ActionMeta;
import net.year4000.utilities.bukkit.gui.IconView;
import net.year4000.utilities.bukkit.gui.InventoryGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

public class TimeView implements IconView {
    static final Map<PlayerPlot.TimeState, ItemStack> MAPPER = ImmutableMap.<PlayerPlot.TimeState, ItemStack>builder()
        .put(PlayerPlot.TimeState.DAWN, ItemUtil.makeItem(Material.WATCH))
        .put(PlayerPlot.TimeState.DAY, ItemUtil.makeItem(Material.WATCH))
        .put(PlayerPlot.TimeState.NIGHT, ItemUtil.makeItem(Material.WATCH))
        .put(PlayerPlot.TimeState.NOON, ItemUtil.makeItem(Material.WATCH))
        .build();
    private PlotManager manager;
    private PlayerPlot.TimeState timeState;

    private TimeView(PlotManager manager, PlayerPlot.TimeState timeState) {
        this.manager = checkNotNull(manager, "manager");
        this.timeState = checkNotNull(timeState, "timeState");
    }

    /** Get the list of al Time Views */
    public static List<TimeView> factory(PlotManager manager) {
        return MAPPER.keySet().stream().map(time -> new TimeView(manager, time)).collect(Collectors.toList());
    }

    @Override
    public ItemStack make() {
        ItemStack icon = MAPPER.get(timeState);
        ItemMeta meta = icon.getItemMeta();
        meta.setDisplayName(Msg.locale(manager.getGamePlayer(), "builders.plot.time.icon", Common.prettyEnum(timeState)));
        icon.setItemMeta(meta);
        return icon;
    }

    @Override
    public boolean action(Player player, ActionMeta meta, InventoryGUI gui) {
        GamePlayer gamePlayer = MapNodes.getCurrentGame().getPlayer(player);
        String out = Common.prettyEnum(timeState);
        player.sendMessage(Msg.locale(gamePlayer, "builders.plot.time.set", out));
        manager.getPlot().setTime(timeState);
        manager.getPlot().addPlotEffects(gamePlayer);
        return true;
    }
}
