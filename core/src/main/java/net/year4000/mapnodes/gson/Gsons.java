/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.year4000.utilities.utils.UtilityConstructError;

/** The handler for Gson */
public final class Gsons {
  public static final Gson GSON = gsonBuilder().create();

  private Gsons() {
    UtilityConstructError.raise();
  }

  /** Normal gson object for standard things */
  public static Gson createGson() {
    return gsonBuilder().create();
  }

  /** Normal gson builder for standard things */
  public static GsonBuilder gsonBuilder() {
    return new GsonBuilder()
      .setVersion(2.0)
      .registerTypeAdapterFactory(new V8TypeAdapterFactory())
      ;
  }
}
