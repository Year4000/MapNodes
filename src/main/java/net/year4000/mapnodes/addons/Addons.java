/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.addons;

import net.year4000.mapnodes.listeners.ListenerBuilder;

import java.util.Map;

public class Addons {
    private static Addons inst = null;
    private AddonBuilder builder = new AddonBuilder();

    /** This this addon instance */
    public static Addons get() {
        if (inst == null) {
            inst = new Addons();
        }
        return inst;
    }

    /** The addon builder */
    public AddonBuilder builder() {
        return builder;
    }

    /** Get the class of the addon */
    public Addon getAddon(Class<? extends Addon> addon) {
        Addon foundAddon = null;

        for (Map.Entry<Addon, ListenerBuilder> addons : builder.getListeners().entrySet()) {
            if (addons.getKey().getClass() == addon) {
                foundAddon = addons.getKey();
                break;
            }
        }

        return foundAddon;
    }
}
