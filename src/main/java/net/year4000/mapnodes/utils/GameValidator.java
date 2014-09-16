package net.year4000.mapnodes.utils;

import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.api.util.Validator;
import net.year4000.mapnodes.exceptions.InvalidJsonException;

public interface GameValidator extends Validator {
    public void validate(GameManager game) throws InvalidJsonException;
}
