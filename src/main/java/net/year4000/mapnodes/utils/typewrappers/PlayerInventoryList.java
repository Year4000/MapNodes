package net.year4000.mapnodes.utils.typewrappers;

import lombok.Getter;
import lombok.Setter;
import net.year4000.mapnodes.game.kits.Item;
import net.year4000.mapnodes.game.kits.SlotItem;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PlayerInventoryList<T> extends ArrayList<T> implements List<T> {
    public PlayerInventoryList(){}

    private List<SlotItem> rawItems = new ArrayList<>();
}
