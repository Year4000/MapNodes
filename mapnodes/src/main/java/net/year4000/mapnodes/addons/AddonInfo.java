/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.addons;

import org.bukkit.event.Listener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AddonInfo {
    /** The name of this add-on */
    public abstract String name();

    /** The version of this add-on */
    public abstract String version();

    /** The description of what this add-on does */
    public abstract String description();

    /** The listener class for this add-on */
    public Class<? extends Listener>[] listeners() default {};

}
