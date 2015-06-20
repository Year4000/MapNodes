/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.api.utils;

import net.year4000.mapnodes.api.exceptions.InvalidJsonException;

public interface Validator {
    public void validate() throws InvalidJsonException;
}
