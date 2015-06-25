/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.game.regions;

import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.api.game.regions.Region;
import net.year4000.mapnodes.api.game.regions.RegionType;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

public final class RegionManager {
    private static RegionManager inst;
    private Set<Class<? extends Region>> rawRegionTypes = new HashSet<>();
    private Map<RegionType, Class<? extends Region>> regionTypes = new HashMap<>();
    private boolean built = false;

    public static RegionManager get() {
        if (inst == null) {
            inst = new RegionManager();
        }

        return inst;
    }

    public RegionManager add(Class<? extends Region> type) {
        rawRegionTypes.add(type);
        return this;
    }

    public void build() {
        rawRegionTypes.forEach(type -> {
            try {
                RegionType typeName = type.getAnnotation(RegionType.class);
                regionTypes.put(typeName, type);
            }
            catch (Exception e) {
                MapNodesPlugin.log(e, false);
            }
        });
        built = true;
    }

    public Class<? extends Region> getRegionType(String name) {
        checkArgument(built); // Make sure we are built

        for (RegionType type : regionTypes.keySet()) {
            if (type.value().isType(name)) {
                return regionTypes.get(type);
            }
        }

        throw new InvalidParameterException(name + " is not a valid region type.");
    }
}
