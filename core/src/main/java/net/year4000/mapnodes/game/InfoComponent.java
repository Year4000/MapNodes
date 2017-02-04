/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes.game;

import com.google.common.collect.Sets;
import net.year4000.utilities.value.Value;

import java.util.Collection;

/** This is the Info Component of the node */
public class InfoComponent {
  private String name;
  private String version;
  private String description;
  private String[] authors;

  /** Get the name of this map or game */
  public String name() {
    return Value.of(name).getOrElse("unknown");
  }

  /** Get the version of this map or game */
  public String version() {
    return Value.of(version).getOrElse("0.0.0");
  }

  /** Get the description of this map or game */
  public String description() {
    return Value.of(description).getOrElse("This map does not have a description");
  }

  /** Get the authors of this map or game, they can be uuid, username, or other */
  public Collection<String> authors() {
    return Sets.newHashSet(authors);
  }

}
