/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.games.agar;

import org.bukkit.util.Vector;

public interface Editable {
    /** Get the current vector location */
    Vector getLocation();

    /** Eat the editable object */
    void eat(Editable editable);
}
