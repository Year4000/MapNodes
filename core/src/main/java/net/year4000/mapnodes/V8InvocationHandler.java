/*
 * Copyright 2019 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import net.year4000.utilities.Conditions;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/** Creates a proxy system to access the Javascript engine */
class V8InvocationHandler implements InvocationHandler {
  /** The object that the methods will execute the Javascript function */
  private final V8 engine;
  private final String lookup;
  private V8Object object;

  V8InvocationHandler(V8 engine, String lookup) {
    this.engine = Conditions.nonNull(engine, "engine");
    this.lookup = Conditions.nonNullOrEmpty(lookup, "lookup");
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if (method.isDefault()) { // Handle Defaults as is
      Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
      constructor.setAccessible(true);
      return constructor.newInstance(proxy.getClass(), MethodHandles.Lookup.PRIVATE)
        .unreflectSpecial(method, proxy.getClass())
        .bindTo(proxy)
        .invokeWithArguments(args);
    }
    // Lazy load the object handling the functions
    if (object == null || object.isReleased()) {
      object = engine.executeObjectScript(lookup);
    }
    // Method names are automatically mapped to java convention of camel case
    return object.executeJSFunction(method.getName(), args);
  }

  /** Release the v8 object */
  @Override
  protected void finalize() throws Throwable {
    if (object != null && !object.isReleased()) {
      object.release();
    }
  }
}
