package net.year4000.mapnodes.utils;

public interface Callback<T> {
    /** The method that gets called once something is done */
    public void callback(T callback, Throwable error);
}

