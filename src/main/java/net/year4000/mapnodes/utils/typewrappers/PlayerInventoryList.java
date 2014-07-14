package net.year4000.mapnodes.utils.typewrappers;

import java.util.ArrayList;
import java.util.List;

public class PlayerInventoryList<T> extends ArrayList<T> implements List<T> {
    public PlayerInventoryList(){}

    public PlayerInventoryList(int size) {
        super(size);
    }
}
