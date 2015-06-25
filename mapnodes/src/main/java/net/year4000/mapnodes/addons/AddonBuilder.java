/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.addons;

import lombok.Getter;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.listeners.ListenerBuilder;
import net.year4000.mapnodes.messages.Msg;

import java.util.*;

public class AddonBuilder {
    private final List<Class<? extends Addon>> addons = new ArrayList<>();
    @Getter
    private final List<AddonInfo> infos = new ArrayList<>();
    @Getter
    private final Map<Addon, ListenerBuilder> listeners = new HashMap<>();

    public AddonBuilder add(Class<? extends Addon> commandClass) {
        addons.add(commandClass);
        return this;
    }

    public void register() {
        addons.forEach(clazz -> {
            try {
                AddonInfo info = clazz.getAnnotation(AddonInfo.class);

                Addon addon = clazz.newInstance();
                addon.start();

                ListenerBuilder builder = new ListenerBuilder();

                for (Class<?> listener : info.listeners()) {
                    builder.add(listener);
                }

                if (info.listeners().length > 0) {
                    builder.register();
                }

                listeners.put(addon, builder);
                infos.add(info);

                MapNodesPlugin.debug(Msg.util("addon.start", info.name(), info.version()));
            }
            catch (InstantiationException | IllegalAccessException e) {
                MapNodesPlugin.debug(e, true);
            }
            catch (NullPointerException e) {
                MapNodesPlugin.debug(Msg.util("addon.invalid", clazz.getSimpleName()));
            }
        });
    }

    /** Unregister the listeners */
    public void unregister() {
        Iterator<AddonInfo> info = infos.iterator();
        listeners.forEach((addon, builder) -> {
            addon.stop();

            builder.unregister();

            AddonInfo current = info.next();
            MapNodesPlugin.debug(Msg.util("addon.stop", current.name(), current.version()));
        });
    }
}
