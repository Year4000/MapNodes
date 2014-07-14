package net.year4000.mapnodes.api.game;

import java.util.List;

public interface GameMap {
    /**
     * Get the name of the map.
     * @return The name of the map.
     */
    public String getName();

    /**
     * Get the version of the map.
     * @return The version of the map.
     */
    public String getVersion();

    /**
     * Get the description of the map.
     * @return The description.
     */
    public String getDescription();

    /**
     * Get the description of the map in a list with the max of seven words.
     * @param multiline Can only be true.
     * @return The list of lines.
     */
    public List<String> getDescription(boolean multiline);

    /**
     * Get the list of all the authors of the map.
     * @return The list of all authors of the map.
     */
    public List<String> getAuthors();

    /**
     * Get the main author of the map.
     * @return The first author in the authors list.
     */
    public String getMainAuthor();

    /**
     * Get the other authors.
     * @return The list of the other authors that is not the main author.
     */
    public List<String> getOtherAuthors();

    /**
     * Does this map have other authors.
     * @return true More than one author.
     */
    public boolean hasOtherAuthors();
}
