/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import com.google.common.base.CaseFormat;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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
  /** The method name cache for the translation of java method names to the javascript function names */
  private final LoadingCache<String, String> methodNameCache = CacheBuilder.<String, String>newBuilder()
    .concurrencyLevel(1) // You can only access the v8 engine with only one thread
    .build(new CacheLoader<String, String>() {
      @Override
      public String load(String key) throws Exception {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, key);
      }
    });

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
    if (object == null || object.isReleased()) { // Lazy load the object handling the functions
      object = engine.executeObjectScript(lookup);
    }
    // Cache the conversion of the java method name to the js function name
    // It is O(n) on first run, n being the length of the string then O(1) if the cache is present
    return object.executeJSFunction(methodNameCache.get(method.getName()), args);
  }
}
