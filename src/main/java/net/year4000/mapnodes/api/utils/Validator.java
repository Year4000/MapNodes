package net.year4000.mapnodes.api.utils;

import net.year4000.mapnodes.api.exceptions.InvalidJsonException;

public interface Validator {
    public void validate() throws InvalidJsonException;
}
