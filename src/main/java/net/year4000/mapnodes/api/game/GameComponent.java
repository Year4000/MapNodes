/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.api.game;

import net.year4000.mapnodes.api.utils.Validator;

public interface GameComponent extends Validator {
    public String getId();

    public GameManager getGame();

    /** Assign node game to the internal class to be used for simple things */
    public void assignNodeGame(GameManager game);
}
