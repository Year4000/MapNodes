/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Locker;
import com.eclipsesource.v8.V8Value;
import net.year4000.utilities.Conditions;

import java.util.concurrent.locks.ReentrantLock;

/** Creates a thread lock for the V8 instance since v8 can only be accessed from one thread at a time */
public class V8ThreadLock<T extends V8Value> implements AutoCloseable {
  private final T instance;
  private final V8Locker locker;
  private final ReentrantLock lock = new ReentrantLock();

  /** Create the lock when this instance is created */
  public V8ThreadLock(T instance) {
    this.instance = Conditions.nonNull(instance, "instance");
    locker = instance.getRuntime().getLocker();
    lock.lock();
    locker.acquire();
  }

  /** Get the instance of the v8 runtime */
  public T v8() {
    return instance;
  }

  /** Release the locks that were placed */
  @Override
  public void close() {
    locker.release();
    lock.unlock();
  }
}
