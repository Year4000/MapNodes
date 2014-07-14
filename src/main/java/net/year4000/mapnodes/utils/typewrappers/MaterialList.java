package net.year4000.mapnodes.utils.typewrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MaterialList<T> extends ArrayList<T> implements List<T> {
    public MaterialList(){}

    public MaterialList(Collection<?> c) {
        super(new ArrayList(c));
    }
}
