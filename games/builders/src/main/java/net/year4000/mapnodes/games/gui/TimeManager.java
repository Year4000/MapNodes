package net.year4000.mapnodes.games.gui;

import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.utilities.bukkit.gui.ActionMeta;
import net.year4000.utilities.bukkit.gui.IconView;
import net.year4000.utilities.bukkit.gui.InventoryGUI;
import net.year4000.utilities.bukkit.items.NBTBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

import static com.google.common.base.Preconditions.checkNotNull;

public class TimeManager extends MapNodesLocaleGUI implements IconView {
    private final int size = (int) Math.ceil(TimeView.MAPPER.size() / 9) + 1;
    private PlotManager manager;

    public TimeManager(PlotManager manager) {
        this.manager = checkNotNull(manager, "manager");
        populateMenu(locale -> {
            generate(locale);
            return Msg.locale(manager.getGamePlayer(), "builders.plot.time");
        }, size);
        MapNodes.getCurrentGame().addTask(SchedulerUtil.repeatSync(this, 10L));
    }

    @Override
    public IconView[][] generate(Locale locale) {
        return populate(size, TimeView.factory(manager).iterator());
    }

    @Override
    public ItemStack make() {
        ItemStack item = TimeView.MAPPER.get(manager.getPlot().getTime());
        NBTBuilder.of(item).setDisplayName(Msg.locale(manager.getGamePlayer(), "builders.plot.time"));
        return item;
    }

    @Override
    public boolean action(Player player, ActionMeta meta, InventoryGUI gui) {
        SchedulerUtil.runSync(() -> openInventory(player));
        return true;
    }
}
