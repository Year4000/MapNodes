/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes.nodes;

import com.google.common.io.ByteStreams;
import net.year4000.mapnodes.MapNodes;
import net.year4000.utilities.Conditions;
import net.year4000.utilities.Utils;

import java.io.*;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

/** Represents a Map package on where to gather the needed objects */
public class MapPackage {
  public static final String PACKAGE_MAP = "map.js";
  public static final String PACKAGE_ICON = "icon.png";
  public static final String PACKAGE_DEFAULT_ICON = "server-icon.png";
  public static final String PACKAGE_WORLD = "world.zip";
  private final String location;
  private byte[] image, map, world;

  /** Convert the URI into the string */
  public MapPackage(URI location) throws IOException {
    this(Conditions.nonNull(location, "location").toString().replaceAll("%20", " "));
  }

  /** Load the package based on the url */
  public MapPackage(String location) throws IOException {
    this.location = Conditions.nonNullOrEmpty(location, "location");
    if (location.startsWith("file://")) {
      // load from tmp folder where the map is cached
      location = location.substring(7); // cut off file://
      if (Files.exists(Paths.get(location).resolve(PACKAGE_ICON))) {
        image = ByteStreams.toByteArray(new FileInputStream(location + "/" + PACKAGE_ICON));
      } else { // Default to ours
        image = ByteStreams.toByteArray(MapNodes.class.getResourceAsStream("/" + PACKAGE_DEFAULT_ICON));
      }
      map = ByteStreams.toByteArray(new FileInputStream(location + "/" + PACKAGE_MAP));
      world = ByteStreams.toByteArray(new FileInputStream(location + "/" + PACKAGE_WORLD));
    } else if (location.startsWith("https://") || location.startsWith("http://")) {
      // load from the API server and cache the results
      // todo when fetching from API server
    }
  }

  /** Get the icon for the map */
  public ByteBuffer image() {
    return ByteBuffer.wrap(image);
  }

  /** Get the map json object */
  public ByteBuffer map() {
    return ByteBuffer.wrap(map);
  }

  /** Get the zip of the world files */
  public ByteBuffer world() {
    return ByteBuffer.wrap(world);
  }

  /** Get the location of this map, can be local or a remote location */
  public String location() {
    return location;
  }

  @Override
  public boolean equals(Object other) {
    return Utils.equals(this, other);
  }

  @Override
  public int hashCode() {
    return location.hashCode();
  }

  @Override
  public String toString() {
    return Utils.toString(this, "location");
  }
}
