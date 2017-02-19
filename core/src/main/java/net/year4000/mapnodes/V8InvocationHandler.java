/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes;

import com.eclipsesource.v8.V8;
import com.google.common.base.CaseFormat;
import net.year4000.utilities.Conditions;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/** Creates a proxy system to access the Javascript engine */
class V8InvocationHandler implements InvocationHandler {
  /** The object that the methods will execute the Javascript function */
  private final V8 engine;
  private final String lookup;

  V8InvocationHandler(V8 engine, String lookup) {
    this.engine = Conditions.nonNull(engine, "engine");
    this.lookup = Conditions.nonNullOrEmpty(lookup, "lookup");
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    try (V8ThreadLock<V8> lock = new V8ThreadLock<>(engine)) {
      String lower = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, method.getName());
      return lock.v8().executeObjectScript(lookup).executeJSFunction(lower, args);
    }
  }
}
