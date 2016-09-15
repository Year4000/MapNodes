/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes.nodes;

import net.year4000.utilities.Utils;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.InputStream;

/** Represents a Map package on where to gather the needed objects */
public class MapPackage {
  private final String location;

  public MapPackage(String location) {
    this.location = location;
    if (location.startsWith("file://")) {
      // load from tmp folder where the map is cached
    } else if (location.startsWith("https://") || location.startsWith("http://")) {
      // load from the API server and cache the results
      // todo when fetching from API server
    }
  }

  /** Get the icon for the map */
  public InputStream image() {
    // todo add the ability to fetch map icon
    return MapPackage.class.getResourceAsStream("server-icon.png");
  }

  /** Get the map json object */
  public InputStream map() {
    throw new NotImplementedException();
  }

  /** Get the zip of the world files */
  public InputStream world() {
    throw new NotImplementedException();
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
