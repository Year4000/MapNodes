package net.year4000.mapnodes;
/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
import com.eclipsesource.v8.V8Object;
import com.google.common.base.CaseFormat;
import net.year4000.utilities.Conditions;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.function.Supplier;

/** Creates a proxy system to acess the Javascript object */
class V8InvocationHandler implements InvocationHandler {
  /** The object that the methods will execute the Javascript function */
  private final Supplier<V8Object> v8Object;

  public V8InvocationHandler(Supplier<V8Object> v8Object) {
    this.v8Object = Conditions.nonNull(v8Object, "v8Object");
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      String lower = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, method.getName());
      return v8Object.get().executeJSFunction(lower, args);
  }
}
