/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes;

import com.eclipsesource.v8.Releasable;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import com.google.common.base.CaseFormat;
import net.year4000.utilities.Utils;

import java.lang.reflect.Method;

/** Create the needed bindings for the javascript functions */
public abstract class Bindings implements Releasable {
  static {
    V8.setFlags("--harmony --use_strict");
  }
  private static V8 engine = V8.createV8Runtime();
  private V8Object object = new V8Object(engine);

  /** Map the java methods to the javascript functions */
  protected Bindings() {
    for (Method method : getClass().getMethods()) {

      String lower = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, method.getName());
      object.registerJavaMethod(this, method.getName(), lower, method.getParameterTypes());
    }
    engine.add("_PLATFORM", "java");
    engine.add("JAVA", object);
  }

  /** Get the b8 instance */
  public V8 v8() {
    return engine;
  }

  /** Release the bindings */
  @Override
  public void release() {
    object.release();
    engine.release();
  }

  /** Print the message */
  public void print(String message) {
    System.out.print(message);
  }

  /** Send a message to a player */
  public abstract void sendMessage(String player, String message);
}
