/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes.nodes;

import com.google.common.io.ByteStreams;
import net.year4000.utilities.Utils;

import java.io.*;
import java.nio.ByteBuffer;

/** Represents a Map package on where to gather the needed objects */
public class MapPackage {
  private final String location;
  private byte[] image, map, world;

  public MapPackage(String location) throws IOException {
    this.location = location;
    if (location.startsWith("file://")) {
      // load from tmp folder where the map is cached
      location = location.substring(7); // cut off file://
      image = ByteStreams.toByteArray(new FileInputStream(location + "/icon.png"));
      map = ByteStreams.toByteArray(new FileInputStream(location + "/map.js"));
      world = ByteStreams.toByteArray(new FileInputStream(location + "/world.zip"));
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
