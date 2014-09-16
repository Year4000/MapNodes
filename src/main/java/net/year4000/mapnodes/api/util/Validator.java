package net.year4000.mapnodes.api.util;

import net.year4000.mapnodes.exceptions.InvalidJsonException;

public interface Validator {
    public void validate() throws InvalidJsonException;
}
