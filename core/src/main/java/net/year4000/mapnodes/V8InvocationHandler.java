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
import com.google.common.cache.RemovalListener;
import net.year4000.utilities.Conditions;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/** Creates a proxy system to access the Javascript engine */
class V8InvocationHandler implements InvocationHandler {
  /** The object that the methods will execute the Javascript function */
  private final V8 engine;
  private final String lookup;
  /** The method name cache for the translation of java method names to the javascript function names */
  private final LoadingCache<String, String> methodNameCache = CacheBuilder.<String, String>newBuilder()
    .concurrencyLevel(1) // You can only access the v8 engine with only one thread
    .build(new CacheLoader<String, String>() {
      @Override
      public String load(String key) throws Exception {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, key);
      }
    });
  /** Cache the V8Object that is grabbed from the v8 engine */
  private final LoadingCache<String, V8Object> objectCache = CacheBuilder.<String, V8Object>newBuilder()
    .concurrencyLevel(1) // You can only access the v8 engine with only one thread
    .expireAfterAccess(1, TimeUnit.SECONDS)
    .removalListener((RemovalListener<String, V8Object>) cached -> {
      V8Object value = cached.getValue();
      if (value != null && !value.isReleased()) {
        value.release();
      }
    })
    .build(new CacheLoader<String, V8Object>() {
      @Override
      public V8Object load(String key) throws Exception {
        return engine.executeObjectScript(key);
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
    // Cache the conversion of the java method name to the js function name
    // It is O(n) on first run, n being the length of the string then O(1) if the cache is present
    return objectCache.getUnchecked(lookup).executeJSFunction(methodNameCache.get(method.getName()), args);
  }
}
