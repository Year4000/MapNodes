package net.year4000.mapnodes.utils;

import net.year4000.mapnodes.exceptions.InvalidJsonException;

public interface Validator {
    public void validate() throws InvalidJsonException;
}
