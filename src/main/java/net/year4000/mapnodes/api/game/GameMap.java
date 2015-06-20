/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.api.game;

import net.year4000.mapnodes.api.utils.Validator;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface GameMap extends Validator {
    /** Get the name of the map */
    public String getName();

    /** Get the version of the map */
    public String getVersion();

    /** Get the description of the map */
    public String getDescription(String locale);

    /** Get the description of the map in a list with the max of seven words */
    public List<String> getMultiLineDescription(String locale);

    /** Get the list of all the authors of the map */
    public List<String> getAuthors();

    /** Get the main author of the map */
    public String getMainAuthor();

    /** Get the other authors */
    public List<String> getOtherAuthors();

    /** Does this map have other authors */
    public boolean hasOtherAuthors();

    public String author(String locale);

    public String author(CommandSender sender);
}
