/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes;

import com.eclipsesource.v8.Releasable;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import com.google.common.base.CaseFormat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/** Create the needed bindings for the javascript functions */
public abstract class Bindings implements Releasable {
  /** Allow method of this class to be bind to the JAVA var in JavaScript */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  @interface Bind {}
  // Set the flags for the V8 runtime
  static {
    V8.setFlags("--harmony --use_strict");
  }
  /** The V8 Runtime for everything */
  private static V8 engine = V8.createV8Runtime();
  /** The V8 Object that is bind to the JAVA var */
  private V8Object object = new V8Object(engine);

  /** Map the java methods to the javascript functions */
  protected Bindings() {
    for (Method method : getClass().getMethods()) {
      if (method.getAnnotation(Bind.class) != null) {
        String lower = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, method.getName());
        object.registerJavaMethod(this, method.getName(), lower, method.getParameterTypes());
      }
    }
    engine.add("_PLATFORM", "java");
    engine.add("JAVA", object);
    engine.getLocker().release(); // release the locker
  }

  /** Get the v8 instance */
  public V8ThreadLock v8Thread() {
    return new V8ThreadLock(engine);
  }

  /** Release the bindings */
  @Override
  public void release() {
    object.release();
    engine.release();
  }

  /** Print the message */
  @Bind public void print(String message) {
    System.out.print(message);
  }

  /** Send a message to a player */
  @Bind public abstract void sendMessage(String player, String message);
}
