/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.utils.typewrappers;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
public class MaterialList<T> extends ArrayList<T> implements List<T> {
    public MaterialList(Collection<?> c) {
        super(new ArrayList(c));
    }
}
